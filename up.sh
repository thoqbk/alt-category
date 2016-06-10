
SSH_PRIVATE_KEY="/Volumes/Hi/xdev/xdoc/ssh/alt"

rm -rf "alt-category.zip"

rm -rf "target"

zip -q -r "alt-category.zip" .

scp -i "$SSH_PRIVATE_KEY" "alt-category.zip" "thoq@52.193.234.156:/home/thoq"

scp -i "$SSH_PRIVATE_KEY" "run.sh" "thoq@52.193.234.156:/home/thoq"

echo "Copy project to server successfully!"