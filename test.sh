#!/bin/bash

set -e
set -o pipefail

tc_pcases=$(find testcases/typecheck/ | rg 'java$' | rg -v 'error.java$')
for f in $tc_pcases
do
  java -cp ./build/classes/java/main:./lib/cs132.jar Compile < $f &>/dev/null
  echo "PASS: $f typechecked as expected"
done

tc_fcases=$(find testcases/typecheck/ | rg 'error.java$')
for f in $tc_fcases
do
  if java -cp ./build/classes/java/main:./lib/cs132.jar Compile < $f &>/dev/null; then
    echo "$f typechecked when it should not have"
  else
    echo "PASS: $f failed to typecheck as expected"
  fi
done

for f in $(find testcases/translate/ | rg 'java$')
do
  if diff <(java -cp ./build/classes/java/main:./lib/cs132.jar Compile < $f | java -jar misc/venus.jar) "$f.out"; then
    echo "PASS: $f"
  else
    echo "FAIL: $f"
  fi
done

