#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
LOG_FILE="${1:-$ROOT_DIR/backend.log}"
PID_FILE="${2:-$ROOT_DIR/backend.pid}"

cd "$BACKEND_DIR"
nohup mvn -B spring-boot:run >"$LOG_FILE" 2>&1 &
echo $! >"$PID_FILE"
