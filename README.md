# SwissSystemMonteCarlo

Programm to simulate Swiss System tournaments. Each simulation is a random walk, where pairings are deterministically created and the results of the resulting pairings are then randomly created. This process is repeated until the last round is complete, which means the random walk has ended. Currently each run completes one random walk, but the option to run mutliple such simulations will soon be added.

# The pairing process:

Since this programm is intended to simulate chess tournaments, it uses a so called dutch system for creating the pairings. https://en.wikipedia.org/wiki/Swiss-system_tournament#Dutch_system

# FIDE Guidelines
https://handbook.fide.com/chapter/C0403 \n
http://pairings.fide.com/images/stories/downloads/appendix_p_approved.pdf
