#!/bin/bash

AUTOGEN="../autogen/autogen.sh"

function add_ext {
  # TODO: If we have spaces in filenames, we're stuffed.
  for X in $(find ./ -name \*.$1); do
    $AUTOGEN -c "Google LLC" --no-tlc --no-code -i $X
  done
}

add_ext java
add_ext py
