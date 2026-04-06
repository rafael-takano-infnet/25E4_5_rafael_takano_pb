#!/usr/bin/env bash
set -euo pipefail

URL="${1:?usage: wait_for_url.sh <url> [retries] [delay_seconds]}"
RETRIES="${2:-30}"
DELAY="${3:-2}"

for attempt in $(seq 1 "$RETRIES"); do
    if curl --fail --silent --show-error "$URL" >/dev/null; then
        echo "URL reachable: $URL"
        exit 0
    fi

    echo "Waiting for $URL ($attempt/$RETRIES)"
    sleep "$DELAY"
done

echo "Timed out waiting for $URL" >&2
exit 1
