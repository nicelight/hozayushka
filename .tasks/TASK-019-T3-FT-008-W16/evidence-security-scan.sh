#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd -- "$script_dir/../.." && pwd)"
cd "$project_root"

known_marker="$(node -e 'const s="com.hozayushka.app.SettingsLocationTest"; let h=0; for (let i=0;i<s.length;i++) h=(Math.imul(31,h)+s.charCodeAt(i))|0; process.stdout.write((h>>>0).toString(16)+"-"+(1704196800000).toString(16));')"
scan_paths=(
  app/src
  .protocols/TASK-019-T3-FT-008-W16
  .tasks/TASK-019-T3-FT-008-W16
  app/build/test-results
  app/build/reports
)

if rg -aFq -- "$known_marker" "${scan_paths[@]}"; then
  printf 'known-marker workspace scan: FAIL\n'
  exit 1
fi

apk_hits=0
while IFS= read -r apk_entry; do
  if unzip -p app/build/outputs/apk/debug/app-debug.apk "$apk_entry" | rg -aFq -- "$known_marker"; then
    apk_hits=$((apk_hits + 1))
  fi
done < <(zipinfo -1 app/build/outputs/apk/debug/app-debug.apk)

if [ "$apk_hits" -ne 0 ]; then
  printf 'known-marker APK scan: FAIL (%s entries)\n' "$apk_hits"
  exit 1
fi

candidate_groups=0
if rg -aIq --pcre2 --glob '!evidence-security-scan.sh' '(?i)(?<![0-9a-f])[0-9a-f]{32}(?![0-9a-f])' "${scan_paths[@]}"; then
  candidate_groups=$((candidate_groups + 1))
fi
if rg -aIq --pcre2 --glob '!evidence-security-scan.sh' '(?i)appid=(?!<redacted>|\[redacted\]|redacted|\*{3})[^&[:space:]"]{8,}' "${scan_paths[@]}"; then
  candidate_groups=$((candidate_groups + 1))
fi
if rg -aIq --pcre2 --glob '!evidence-security-scan.sh' '(?i)(?:api[_-]?key|token|secret)[A-Za-z0-9_]*[[:space:]]*=[[:space:]]*"(?=[A-Za-z0-9._-]{8,}")(?=[A-Za-z0-9._-]*[0-9])[A-Za-z0-9._-]{8,}"' "${scan_paths[@]}"; then
  candidate_groups=$((candidate_groups + 1))
fi

if [ "$candidate_groups" -ne 0 ]; then
  printf 'credential-literal scan: FAIL (%s candidate groups)\n' "$candidate_groups"
  exit 1
fi

aapt2=/home/serg/Android/Sdk/build-tools/34.0.0/aapt2
if ! "$aapt2" dump resources app/build/outputs/apk/debug/app-debug.apk \
  | rg -q 'settings_(open_meteo_attribution|open_weather_key|geonames_attribution|back)|open-meteo\.com|creativecommons\.org/licenses/by/4\.0'; then
  printf 'packaged-resource static scan: FAIL\n'
  exit 1
fi

printf 'known-marker scan: PASS (0 workspace hits; 0 decompressed APK entry hits)\n'
printf 'credential-literal scan: PASS (0 candidate groups; preference-key identifiers excluded by value-shape)\n'
printf 'packaged-resource static scan: PASS\n'
