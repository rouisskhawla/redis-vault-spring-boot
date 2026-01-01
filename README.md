# Spring Boot with Redis & HashiCorp Vault Integration

This guide explains how to securely connect a Spring Boot application to Redis using credentials stored in HashiCorp Vault. Vault manages secrets, including TLS certificates and Redis credentials, while Spring Boot retrieves them at runtime using AppRole authentication.

Both Vault and Redis are installed on separate VMs with TLS enabled. Certificates are imported into local Java KeyStore to allow the Spring Boot app to run locally.

---

## Table of Contents

1. [Vault & Redis Certificates](#vault--redis-certificates)
2. [Vault Unseal & Auto-Unseal](#vault-unseal--auto-unseal)
3. [Vault AppRole Authentication](#vault-approle-authentication)
4. [Spring Boot Configuration](#spring-boot-configuration)

---

## Vault & Redis Certificates

1. Imported Vault and Redis TLS certificates into local Java KeyStore:

```bash
keytool -importcert \
  -alias vault \
  -file path\to\vault-ca.pem \
  -keystore "C:\Program Files\Java\jdk-17\lib\security\cacerts"

keytool -importcert \
  -alias redis \
  -file path\to\redis-ca.pem \
  -keystore "C:\Program Files\Java\jdk-17\lib\security\cacerts"
```

2. Prepare Redis certificate as a single-line JSON-ready string for Vault:

```bash
awk 'BEGIN {printf "\""} {gsub(/\r?\n/, "\\n"); printf "%s\\n", $0} END {print "\""}' redis-ca.crt
```

3. Added Redis properties to the Vault secret:

```json
{
  "redis.caCert": "<single-line JSON cert>",
  "redis.dataModelTtl": 3600,
  "redis.host": "192.168.1.9",
  "redis.username": "user",
  "redis.password": "password",
  "redis.port": 6379
}
```

---

## Vault Unseal & Auto-Unseal

Vault is configured to run sealed by default and automatically unseal itself after reboots or service restarts.

### 1. Listener Configuration

Vault listens on TCP port 8200 with TLS:

**`/etc/vault.d/vault.hcl`**

```hcl
listener "tcp" {
  address        = "0.0.0.0:8200"
  tls_cert_file  = "/opt/vault/tls/tls.cert"
  tls_key_file   = "/opt/vault/tls/tls.key"
}
```

* TLS certificate acts as server certificate and CA.
* Self-signed with SANs for `192.168.1.8` and `localhost`.

TLS files location:

```
/opt/vault/tls/
├── tls.cert
├── tls.key
└── tls.crt
```

### 2. Environment Variables

**`/etc/vault.d/vault.env`**

```bash
VAULT_ADDR=https://192.168.1.8:8200
VAULT_CACERT=/opt/vault/tls/tls.cert
```

### 3. Unseal Keys

**`/opt/vault/unseal_keys.txt`**

```
unseal-key-1
unseal-key-2
unseal-key-3
```

* Permissions: `-rw------- vault vault`
* Only the `vault` user can read this file.

### 4. Auto-Unseal Script

**Location:** `/opt/vault/auto-unseal.sh`

```bash
#!/usr/bin/env bash
set -e
source /etc/vault.d/vault.env

for i in {1..30}; do
  if vault status >/dev/null 2>&1; then break; fi
  sleep 2
done

if vault status | grep -q 'Sealed.*false'; then exit 0; fi

while read -r key; do
  vault operator unseal "$key" || true
  sleep 1
done < /opt/vault/unseal_keys.txt
```

* Permissions: `-rwx------ vault vault`

### 5. systemd Auto-Unseal Service

**File:** `/etc/systemd/system/vault-auto-unseal.service`

```ini
[Unit]
Description=Vault Auto Unseal
After=vault.service
PartOf=vault.service

[Service]
Type=oneshot
User=vault
Group=vault
EnvironmentFile=/etc/vault.d/vault.env
ExecStart=/opt/vault/auto-unseal.sh

[Install]
WantedBy=vault.service
```

**Enable service:**

```bash
sudo systemctl daemon-reload
sudo systemctl enable vault-auto-unseal
```

**Verify:**

```bash
systemctl status vault
systemctl status vault-auto-unseal
vault status
```

Expected output: `Sealed: false`

---

## Vault AppRole Authentication

### 1. Enable AppRole

```bash
vault auth enable approle
```

### 2. Create Policy

**`redis-policy`** grants access to Redis secrets:

```hcl
path "prod/data/redis/server" {
  capabilities = ["create", "read", "update", "delete", "list"]
}
```

### 3. Create AppRole

```bash
vault write auth/approle/role/my-role token_policies="redis-policy"
```

### 4. Retrieve Role ID & Secret ID

**Role ID:**

```bash
vault read auth/approle/role/my-role/role-id
```

**Secret ID:**

```bash
vault write -f auth/approle/role/my-role/secret-id
```

### 5. Spring Boot Configuration

```properties

spring.cloud.vault.uri=https://192.168.1.8:8200
spring.cloud.vault.authentication=approle
spring.cloud.vault.app-role.role-id=<ROLE_ID>
spring.cloud.vault.app-role.secret-id=<SECRET_ID>

spring.cloud.vault.kv.enabled=true
spring.cloud.vault.kv.backend=prod
spring.cloud.vault.kv.default-context=redis/server

spring.config.import=vault://
```

Spring Boot will fetch Redis credentials and TLS certificate from Vault at startup.

---

## Execution Flow

1. System boots or Vault service restarts.
2. Vault starts sealed.
3. `vault-auto-unseal.service` triggers the auto-unseal script.
4. Script submits unseal keys sequentially.
5. Vault becomes unsealed and operational.
6. Spring Boot authenticates using AppRole.
7. Redis credentials and CA certificate are retrieved securely from Vault.
8. Application connects to Redis over TLS.
