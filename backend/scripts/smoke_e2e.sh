#!/usr/bin/env bash
set -euo pipefail

BASE_URL=${BASE_URL:-http://localhost:8080}

COOKIE_HOST=/tmp/walkservice-cookie-host.txt
COOKIE_PART=/tmp/walkservice-cookie-part.txt

rm -f "$COOKIE_HOST" "$COOKIE_PART"

request() {
  local method=$1
  local url=$2
  local cookie=$3
  local body=${4:-}

  if [[ -n "$cookie" && -f "$cookie" ]]; then
    if [[ -n "$body" ]]; then
      curl -sS -b "$cookie" -c "$cookie" -H 'Content-Type: application/json' -X "$method" -d "$body" "$url"
    else
      curl -sS -b "$cookie" -c "$cookie" -X "$method" "$url"
    fi
    return
  fi

  if [[ -n "$cookie" ]]; then
    if [[ -n "$body" ]]; then
      curl -sS -c "$cookie" -H 'Content-Type: application/json' -X "$method" -d "$body" "$url"
    else
      curl -sS -c "$cookie" -X "$method" "$url"
    fi
    return
  fi

  if [[ -n "$body" ]]; then
    curl -sS -H 'Content-Type: application/json' -X "$method" -d "$body" "$url"
  else
    curl -sS -X "$method" "$url"
  fi
}

json_get() {
  local path=$1
  python3 -c 'import json,sys
obj=json.load(sys.stdin)
path=sys.argv[1]
cur=obj
for part in path.split("."):
    if part.isdigit():
        cur=cur[int(part)]
    else:
        cur=cur[part]
print(cur)
' "$path"
}

echo "[1] unauthenticated /users/me should be 401"
CODE=$(curl -s -o /dev/null -w '%{http_code}' "$BASE_URL/api/v1/users/me" || true)
if [[ "$CODE" != "401" ]]; then
  echo "Expected 401, got $CODE"
  exit 1
fi

echo "[2] login host"
HOST_LOGIN=$(request POST "$BASE_URL/api/v1/login" "$COOKIE_HOST" '{"email":"host@example.com"}')
HOST_ID=$(echo "$HOST_LOGIN" | json_get data.id)

echo "[3] login participant"
PART_LOGIN=$(request POST "$BASE_URL/api/v1/login" "$COOKIE_PART" '{"email":"participant@example.com"}')
PART_ID=$(echo "$PART_LOGIN" | json_get data.id)

echo "[4] host creates dog"
request POST "$BASE_URL/api/v1/dogs" "$COOKIE_HOST" '{"name":"Buddy","breed":"Maltese","size":"SMALL","neutered":true,"sociabilityLevel":"MEDIUM","reactivityLevel":"LOW","notes":""}' > /dev/null

echo "[5] host creates meetup"
SCHEDULED_AT=$(python3 - <<'PY'
from datetime import datetime,timezone,timedelta
print((datetime.now(timezone.utc)+timedelta(days=1)).isoformat().replace('+00:00','Z'))
PY
)

MEETUP_CREATE=$(request POST "$BASE_URL/api/v1/meetups" "$COOKIE_HOST" "{\"title\":\"Walk\",\"description\":\"\",\"location\":\"Park\",\"maxParticipants\":2,\"scheduledAt\":\"$SCHEDULED_AT\"}")
MEETUP_ID=$(echo "$MEETUP_CREATE" | json_get data.id)

echo "[6] participant requests participation"
PART_REQ=$(request POST "$BASE_URL/api/v1/meetups/$MEETUP_ID/participations" "$COOKIE_PART")
PARTICIPATION_ID=$(echo "$PART_REQ" | json_get data.id)

echo "[7] host approves participation"
request POST "$BASE_URL/api/v1/meetups/$MEETUP_ID/participations/$PARTICIPATION_ID/approve" "$COOKIE_HOST" > /dev/null

echo "[8] host ends meetup"
request POST "$BASE_URL/api/v1/meetups/$MEETUP_ID/end" "$COOKIE_HOST" > /dev/null

echo "[9] host creates communication"
request POST "$BASE_URL/api/v1/meetups/$MEETUP_ID/communications" "$COOKIE_HOST" '{"content":"hello"}' > /dev/null

echo "[10] host creates report and block for participant"
request POST "$BASE_URL/api/v1/safety/reports" "$COOKIE_HOST" "{\"reportedUserId\":$PART_ID,\"meetupId\":$MEETUP_ID,\"reason\":\"spam\"}" > /dev/null
request POST "$BASE_URL/api/v1/safety/blocks" "$COOKIE_HOST" "{\"blockedUserId\":$PART_ID}" > /dev/null

echo "OK"
