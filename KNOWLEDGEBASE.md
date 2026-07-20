# Untangle Knowledge Base

This document is the durable product and implementation reference for the
Untangle Android project.

## Product identity

- App name: **Untangle**
- Platform: Android
- Package: `com.hamidzar2002.untangle`
- Distribution: free on Google Play
- Monetisation: Google AdMob ads
- Core promise: move points until none of the connecting lines cross

## Instructions: Untangle

You are given a number of points, some of which have lines drawn between them.
You can move the points about arbitrarily; your aim is to position the points
so that no line crosses another.

Simon Tatham originally saw this in the form of a Flash game called
[Planarity][planarity], written by John Tantalo.

### Untangle controls

In the original desktop version, a point is moved by clicking it with the left
mouse button and dragging it into a new position.

The cursor keys may also be used to navigate amongst the points. Pressing the
Enter key toggles dragging the currently highlighted point. Pressing Tab or
Space cycles through all the points.

The Android interaction must translate those controls naturally:

- Touch and drag a point to reposition it.
- Make points large enough to acquire comfortably without hiding their exact
  centre.
- Keep keyboard/D-pad focus support for Chromebooks, tablets, and accessibility.
- Clearly distinguish the selected or actively dragged point.
- Detect completion as soon as no two non-adjacent edges cross.

### Untangle parameters

There is one core custom parameter:

#### Number of points

Controls the size of the puzzle by specifying the number of points in the
generated graph.

The Android UI may present friendly presets in addition to a custom point
count, but the underlying puzzle parameter remains the number of points.

## Gameplay requirements

- Generate a planar graph, then scramble its point positions to create crossings.
- The scrambled puzzle must contain at least one crossing.
- Dragging changes point positions only; it does not change graph connectivity.
- Lines sharing an endpoint do not count as crossings.
- A puzzle is solved when every pair of non-adjacent line segments is
  non-intersecting.
- Support new puzzle, restart, and completion feedback.
- Preserve the current unfinished puzzle across process death and app restarts.
- Keep the puzzle playable offline after installation; ads may fail gracefully
  when the device is offline.

## Source references

The implementation should be an idiomatic Kotlin/Jetpack Compose
reimplementation informed by Simon Tatham's original algorithms, rather than
embedding the C UI directly.

- User-selected C reference:
  [kbarni/kindlepuzzles `untangle.c`][kindle-untangle]
- Original collection and playable reference:
  [Simon Tatham's Portable Puzzle Collection][puzzles]
- Original Untangle manual:
  [Chapter 18: Untangle][untangle-manual]
- Existing open-source Android port:
  [chrisboyle/sgtpuzzles][android-port]

The C reference is valuable for:

- planar graph generation;
- puzzle description and parameter handling;
- segment intersection rules;
- completion detection;
- input semantics and rendering-state behaviour.

The Android app follows Model-View-Controller (MVC), adapted for lifecycle-aware
Jetpack Compose:

- `model`: pure Kotlin points, edges, puzzle state, generation, segment
  intersection, crossing count, and completion rules. The model has no Android
  or Compose dependencies.
- `view`: stateless Compose screens and canvas rendering. Views receive
  immutable model values and emit user-action callbacks; they do not mutate
  game state or implement puzzle rules.
- `controller`: an Android `ViewModel` that owns observable state and translates
  view actions into model operations. It contains coordination logic, not
  geometry or rendering.

Supporting services remain outside the MVC core:

- `data`: puzzle persistence, settings, and statistics;
- `ads`: consent, SDK initialisation, and ad presentation.

Dependency direction is `View -> Controller -> Model`. The model never imports
the controller or view, and the controller never imports Compose UI.

## Licence and attribution

Simon Tatham's Portable Puzzle Collection is distributed under the MIT
Licence. Any copied or substantially derived source must retain the upstream
copyright and permission notice.

Before the first public build:

- add the complete upstream MIT text and copyright notice to `THIRD_PARTY_NOTICES.md`;
- add an in-app Open Source Licences entry;
- identify substantially ported algorithms in source comments;
- do not copy branding, store graphics, icons, or other artwork from third-party
  Android releases unless their licence explicitly permits it;
- retain attribution even though the app is free and ad-supported.

## Advertising model

Untangle will be free and supported by Google AdMob.

### Production AdMob identifiers

- AdMob App ID: `ca-app-pub-6961751302262101~7558692283`
- Banner ad-unit ID: `ca-app-pub-6961751302262101/5961559645`

AdMob application and ad-unit IDs identify ad inventory; they are not account
credentials or secrets. AdMob login credentials, payment information, API
private keys, and service-account credentials must never be committed.

Implemented approach:

- Use GMA Next-Gen SDK `1.2.1` and Google User Messaging Platform (UMP) SDK
  `4.0.0`.
- Show a standard adaptive banner outside the active puzzle board so the ad
  never covers points, lines, controls, or completion feedback.
- If interstitial ads are added, show them only at natural breaks such as after
  a completed puzzle, never during dragging or immediately on app launch.
- Do not show an interstitial after every puzzle; use a conservative frequency
  cap.
- Never make an accidental ad tap likely through layout movement or proximity
  to game controls.
- Use Google's official test ad IDs in all debug builds. Release builds use the
  production identifiers recorded above.
- Request/update consent at every launch as required by UMP, expose a privacy
  options entry point when required, and do not request ads until consent state
  permits it.
- Provide a public privacy policy before the Play release.
- Complete Google Play's **Contains ads** declaration and Data safety form
  based on the actual SDK configuration.
- The game must remain functional when an ad is unavailable, blocked, or
  offline.

The integration lives in `ads/AdsManager.kt` and the banner Compose view in
`view/UntangleScreen.kt`. Debug builds use Google's sample app and banner IDs;
release builds use the production IDs above.

Google's current Next-Gen Mobile Ads SDK documentation is the implementation
authority:

- [GMA Next-Gen Android quick start][gma-quick-start]
- [UMP privacy and consent setup][ump]
- [Official Google Mobile Ads Android examples][google-ads-samples]

## Decisions still needed

- Initial point-count presets and upper/lower limits.
- Hint behaviour, if any.
- Banner-only launch versus banner plus completion interstitials.
- Final visual identity, launcher icon, and store assets.

## Privacy policy

- Source: `untangle-privacy-policy.html`
- Public URL:
  `https://hamidzar2002.github.io/untangle/untangle-privacy-policy.html`
- Hosting: GitHub Pages, deployed by `.github/workflows/pages.yml`

## Android release signing

Untangle uses a dedicated upload key with Google Play App Signing. The private
keystore and its passwords must remain outside the repository.

Local signing files:

- Signing directory: `/mnt/c/Users/earmzah/games/untangle-signing`
- Upload keystore: `untangle-upload.jks`
- Public upload certificate: `untangle-upload-certificate.pem`
- GitHub secret values: `github-actions-secrets.txt`
- Certificate owner: `CN=Hamid Zarrazvand, O=Untangle, C=IE`
- SHA-1: `36:A1:76:EB:B3:00:0D:E5:D4:70:0D:0E:11:15:48:8A:B7:E4:D6:E3`
- SHA-256:
  `F6:97:5D:B4:56:B6:18:97:3F:2E:C2:A0:6C:D7:26:4D:9B:F5:D8:A4:E8:83:8F:21:9E:63:04:3D:DD:B0:90:A4`

GitHub Actions release signing requires these repository secrets:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

On pushes to `main` and manual runs, `.github/workflows/android.yml` decodes the
keystore into the temporary runner directory, builds a signed release AAB, and
uploads it as `untangle-release-aab`. Pull requests run tests and build the
debug APK without receiving signing secrets.

Never commit the signing directory, keystore, Base64 value, or passwords. Back
up the upload keystore securely; future releases for
`com.hamidzar2002.untangle` must use the same registered upload key unless an
upload-key reset is completed through Google Play.

[planarity]: http://planarity.net
[kindle-untangle]: https://github.com/kbarni/kindlepuzzles/blob/main/untangle.c
[puzzles]: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/
[untangle-manual]: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/doc/untangle.html
[android-port]: https://github.com/chrisboyle/sgtpuzzles
[gma-quick-start]: https://developers.google.com/admob/android/next-gen/quick-start
[ump]: https://developers.google.com/admob/android/privacy
[google-ads-samples]: https://github.com/googleads/googleads-mobile-android-examples
