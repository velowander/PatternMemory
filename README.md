## PatternMemory

Android Studio project; simple "Simon" (electronic game) clone. Reproduce the pattern on 4 colored keys. No sound.
Runs on Android API 9 (2.3 Gingerbread) and above, though could be easily modified to run on API 7 (Eclair) and perhaps lower.
* Abstraction of controller from presentation (the "Simon" game controller doesn't know anything about what the board looks like or even how many keys there are until told).
* Use of Fragments to encapsulate different aspects of the game.
* Java Observer / Observable to broadcast score and round changes to the ScoreBar.
