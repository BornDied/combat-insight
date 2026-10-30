# Local testing preflight — October 25, 2026

**Submission follow-up:** [alpha4 scope, page wording and verification](2026-10-25-alpha4-submission.md). Local checkpoint results below are retained as history.

Build: **0.9.0-alpha4-local.1**, branch `local/archive-completion`. No push, PR change, merge, release or Plugin Hub submission.

## Patch found before manual testing

With a resolved target but unknown current HP, the enchanted-bolt calculation correctly withheld DPS and Zaryte ruby-special damage. However, snapshot maximum availability stayed true, causing the HUD to fall back to the ordinary, unenchanted bolt maximum. That number did not describe the unresolved ruby effect. The ordinary accuracy could likewise omit the proc contribution.

Two new equipped-snapshot tests reproduced the problem, including a matching boss HUD rejected because it was a shared extra bar. The snapshot now withholds maximum and combined accuracy when the enchanted-bolt result is unavailable. The Inspector retains the reason; the combat HUD uses `?`. Plain bolts remain available without HP. A valid matching boss HUD still takes priority over a conflicting manual party input and supplies ruby damage.

## Additional coverage

- Unknown/shared-bar ruby HP cannot publish the ordinary maximum.
- Known observed boss HP overrides manual party HP:100 current HP yields Zaryte ruby22, reduced by observed Bork armour8 to14.
- Plain bolts still calculate without target HP.
- Ezekiel enrage and imp-buff uncertainty block Voidwaker, claws, Ralos and Zaryte special previews as well as ordinary DPS.
- Enrage state does not carry into a different boss actor.
- Long unavailable special reasons render as `?` without an active special accent.

Seven tests were added; there are no other production changes beyond the bolt-result availability guard and version metadata.

## Results

- `gradlew.bat clean test check build shadowJar --no-daemon --console=plain`: **BUILD SUCCESSFUL in15s;8 tasks executed**.
- **336 Java tests /73 suites;0 failures,0 errors,0 skips.**
- `python -m unittest discover -s scripts -p 'test_*.py'`: **12 tests, OK**.
- `java -ea -jar build/libs/combat-insight-0.9.0-alpha4-local.1-all.jar --help`: **exit0**, packaged entry point and command-line parser work. Without `-ea`, RuneLite intentionally rejects the developer entry point; the existing Gradle `run` task already enables assertions.
- Diff whitespace and intended-file review passed. No gameplay, account login, actual client GUI startup or in-game verification was performed. The command-line check is not a plugin-injection or gameplay test.

## Before a full raid test

1. Launch this project with `gradlew.bat run`. Confirm diagnostic version `0.9.0-alpha4-local.1`, plugin off/on and Inspector open/copy actions. Watch for runtime errors.
2. On a dummy or ordinary target, check all three HUD modes, gear/prayer/style changes, saved-spell isolation, and blowpipe dart/Rapid inputs. Unknown special descriptions must remain compact.
3. Then test the [prioritized Archive checklist](2026-10-24-archive-completion.md#prioritized-live-checklist), collecting NPC IDs and diagnostics around transitions. Remaining formula gaps documented there still apply; passing this preflight is not complete raid support.
