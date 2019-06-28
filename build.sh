#!/usr/bin/env bash


CMD="$1"
shift


build () {

lein test && lein install

}

release () {

lein deploy

}

case "$CMD" in

   release )
     release
     ;;
   build )
     build
     ;;
   * )
   echo "./build.sh build|release"
   ;;
esac
