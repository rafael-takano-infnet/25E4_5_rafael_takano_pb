#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
LOG_FILE_INPUT="${1:-$ROOT_DIR/backend.log}"
PID_FILE_INPUT="${2:-$ROOT_DIR/backend.pid}"

if [[ "$LOG_FILE_INPUT" = /* ]]; then
    LOG_FILE="$LOG_FILE_INPUT"
else
    LOG_FILE="$ROOT_DIR/$LOG_FILE_INPUT"
fi

if [[ "$PID_FILE_INPUT" = /* ]]; then
    PID_FILE="$PID_FILE_INPUT"
else
    PID_FILE="$ROOT_DIR/$PID_FILE_INPUT"
fi

mkdir -p "$(dirname "$LOG_FILE")" "$(dirname "$PID_FILE")"

nohup bash -lc "cd \"$BACKEND_DIR\" && exec mvn -B spring-boot:run" >"$LOG_FILE" 2>&1 &
echo $! >"$PID_FILE"
