#!/usr/bin/env bash
# generate_launcher_icons.sh
SRC=/Users/admin/Downloads/cat_tracker_icon2.png
DST=app/src/main/res/mipmap-mdpi

declare -A SIZES=(
  [mipmap-mdpi]=48
  [mipmap-hdpi]=72
  [mipmap-xhdpi]=96
  [mipmap-xxhdpi]=144
  [mipmap-xxxhdpi]=192
)

for DIR in "${!SIZES[@]}"; do
  SIZE=${SIZES[$DIR]}
  mkdir -p "$DST/$DIR"
  convert "$SRC" -resize "${SIZE}x${SIZE}" "$DST/$DIR/ic_launcher.png"
  # 可选：圆角图标
  convert "$SRC" -resize "${SIZE}x${SIZE}" "$DST/$DIR/ic_launcher_round.png"
done
