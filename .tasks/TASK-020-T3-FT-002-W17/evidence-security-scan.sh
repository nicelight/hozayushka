#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd -- "$script_dir/../.." && pwd)"
cd "$project_root"

apk_path="app/build/outputs/apk/debug/app-debug.apk"
test_marker="$(node -e 'const s="com.hozayushka.app.SettingsLocationTest"; let h=0; for (let i=0;i<s.length;i++) h=(Math.imul(31,h)+s.charCodeAt(i))|0; process.stdout.write((h>>>0).toString(16)+"-"+(1704196800000).toString(16));')"
scan_paths=(
  app/src
  .protocols/TASK-020-T3-FT-002-W17
  .tasks/TASK-020-T3-FT-002-W17
  app/build/test-results
  app/build/reports
)

if [ ! -f "$apk_path" ]; then
  printf 'APK scan: FAIL (debug APK missing)\n'
  exit 1
fi

if rg -aFq --glob '!evidence-security-scan.sh' -- "$test_marker" "${scan_paths[@]}"; then
  printf 'known-marker workspace scan: FAIL\n'
  exit 1
fi

candidate_groups=0
if rg -aIq --pcre2 --glob '!evidence-security-scan.sh' '(?i)(?<![0-9a-f])[0-9a-f]{32}(?![0-9a-f])' "${scan_paths[@]}"; then
  candidate_groups=$((candidate_groups + 1))
fi
if rg -aIq --pcre2 --glob '!evidence-security-scan.sh' '(?i)appid=(?!%s|<redacted>|\[redacted\]|redacted|\*{3})[A-Za-z0-9._~-]{8,}' "${scan_paths[@]}"; then
  candidate_groups=$((candidate_groups + 1))
fi
if rg -aIq --pcre2 --glob '!evidence-security-scan.sh' '(?i)(?:api[_-]?key|token|secret)[A-Za-z0-9_]*[[:space:]]*=[[:space:]]*"(?=[A-Za-z0-9._-]{8,}")(?=[A-Za-z0-9._-]*[0-9])[A-Za-z0-9._-]{8,}"' "${scan_paths[@]}"; then
  candidate_groups=$((candidate_groups + 1))
fi
if [ "$candidate_groups" -ne 0 ]; then
  printf 'credential-literal workspace scan: FAIL (%s candidate groups)\n' "$candidate_groups"
  exit 1
fi

provider_implementations="$(rg -l '\) : WeatherProvider' app/src/main/kotlin/com/hozayushka/app/adapters/weather --glob '*.kt' | sort)"
expected_implementations="$(printf '%s\n' \
  'app/src/main/kotlin/com/hozayushka/app/adapters/weather/OpenMeteoWeatherAdapter.kt' \
  'app/src/main/kotlin/com/hozayushka/app/adapters/weather/OpenWeatherWeatherAdapter.kt' | sort)"
if [ "$provider_implementations" != "$expected_implementations" ]; then
  printf 'production-provider inventory scan: FAIL\n'
  exit 1
fi
if rg -aIq '(?i)yandex|api\.weather\.yandex|X-Yandex-Weather-Key' app/src/main; then
  printf 'legacy-provider source scan: FAIL\n'
  exit 1
fi

apk_marker_hits=0
apk_credential_hits=0
apk_legacy_hits=0
while IFS= read -r apk_entry; do
  if unzip -p "$apk_path" "$apk_entry" | rg -aFq -- "$test_marker"; then
    apk_marker_hits=$((apk_marker_hits + 1))
  fi
  if unzip -p "$apk_path" "$apk_entry" | rg -aIq --pcre2 '(?i)appid=(?!%s|<redacted>|\[redacted\]|redacted|\*{3})[A-Za-z0-9._~-]{8,}'; then
    apk_credential_hits=$((apk_credential_hits + 1))
  fi
  if unzip -p "$apk_path" "$apk_entry" | rg -aIq '(?i)yandex|api\.weather\.yandex|X-Yandex-Weather-Key'; then
    apk_legacy_hits=$((apk_legacy_hits + 1))
  fi
done < <(zipinfo -1 "$apk_path")

if [ "$apk_marker_hits" -ne 0 ] || [ "$apk_credential_hits" -ne 0 ] || [ "$apk_legacy_hits" -ne 0 ]; then
  printf 'decompressed APK scan: FAIL (marker=%s credential=%s legacy=%s)\n' \
    "$apk_marker_hits" "$apk_credential_hits" "$apk_legacy_hits"
  exit 1
fi

printf 'known-marker scan: PASS (0 workspace; 0 decompressed APK entries)\n'
printf 'credential-literal scan: PASS (0 workspace candidate groups; 0 APK entries)\n'
printf 'production-provider inventory: PASS (Open-Meteo + OpenWeather only)\n'
printf 'legacy-provider scan: PASS (0 production-source; 0 APK entries)\n'
