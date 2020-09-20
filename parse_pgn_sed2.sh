#!/bin/sh

#remove \r from regexes in first awk call if the file does not contain carriage returns

if [ $# -ne 1 ]; then
	echo 'Please give the pgn file as the only argument.'
	exit
fi

sed -n '
/^\[Result "\(.\+\)\-.\+"\]\r$/ {
	s/^\[Result "\(.\+\)\-.\+"\]\r$/\1/
	s/^1$/2,/
	s/^1\/2$/1,/
	s/^0$/0,/
	h
	b
}
/^\[WhiteElo "\([0-9]\+\)"\]\r$/ {
	s/^\[WhiteElo "\([0-9]\+\)"\]\r$/\1,/
	H
	b
}
/^\[BlackElo "\([0-9]\+\)"\]\r$/ {
	s/^\[BlackElo "\([0-9]\+\)"\]\r$/\1/
	H
	g
	s/\n//g
	/[012],[0-9]\+,[0-9]\+/p
}
' $1
