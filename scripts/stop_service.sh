#!/usr/bin/env bash
set -euo pipefail

PID_FILE="${1:?usage: stop_service.sh <pid_file>}"

if [[ -f "$PID_FILE" ]]; then
    PID="$(cat "$PID_FILE")"
    if [[ -n "$PID" ]] && kill -0 "$PID" 2>/dev/null; then
        kill "$PID" || true
        wait "$PID" 2>/dev/null || true
    fi
    rm -f "$PID_FILE"
fi
