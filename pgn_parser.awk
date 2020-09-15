/^\[Result "0-1"\]/ {printf "0,"}
/^\[Result "1\/2-1\/2"\]/ {printf "0.5,"}
/^\[Result "1-0"\]/ {printf "1,"}
/^\[WhiteElo "[0-9]{3}"\]/ {printf "%s,", substr($2, 2, 3)}
/^\[WhiteElo "[0-9]{4}"\]/ {printf "%s,", substr($2, 2, 4)}
/^\[BlackElo "[0-9]{3}"\]/ {printf "%s", substr($2, 2, 3)}
/^\[BlackElo "[0-9]{4}"\]/ {printf "%s", substr($2, 2, 4)}
END {printf "\n"}
