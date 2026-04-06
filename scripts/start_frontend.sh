#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONTEND_DIR="$ROOT_DIR/frontend"
LOG_FILE="${1:-$ROOT_DIR/frontend.log}"
PID_FILE="${2:-$ROOT_DIR/frontend.pid}"

cd "$FRONTEND_DIR"
nohup npm run dev -- --host 0.0.0.0 >"$LOG_FILE" 2>&1 &
echo $! >"$PID_FILE"
