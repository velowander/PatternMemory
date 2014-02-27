PatternMemory
=============

Android Studio 0.46 project; simple "Simon" (electronic game) clone. Reproduce the pattern on 4 colored keys. No sound.
Runs on Android API 8 (2.2 Froyo) and above.
Abstraction of controller from presentation (the "Simon" game controller doesn't know anything about what the board looks like or even how many keys there are until told).
Use of Fragments to encapsulate different aspects of the game.
Java Observer / Observable to broadcast score and round changes to the ScoreBar.
