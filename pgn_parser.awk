/^\[Result "0-1"\]/ {printf "\n0,"}
/^\[Result "1\/2-1\/2"\]/ {printf "\n0.5,"}
/^\[Result "1-0"\]/ {printf "\n1,"}
/^\[WhiteElo "[0-9]{3}"\]/ {printf "%s,", substr($2, 2, 3)}
/^\[WhiteElo "[0-9]{4}"\]/ {printf "%s,", substr($2, 2, 4)}
/^\[BlackElo "[0-9]{3}"\]/ {printf "%s", substr($2, 2, 3)}
/^\[BlackElo "[0-9]{4}"\]/ {printf "%s", substr($2, 2, 4)}
END {printf "\n"}
