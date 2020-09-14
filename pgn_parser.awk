/^\[Result "0-1"\]$/ {printf "0,"}
/^\[Result "1\/2-1\/2"\]$/ {printf "0.5,"}
/^\[Result "1-0"\]$/ {printf "1,"}
/^\[WhiteElo "[0-9]*"\]$/ {printf "%s,", $2}
/^\[BlackElo "[0-9]*"\]$/ {print $2}
