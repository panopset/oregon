if [ -z "$1"]; then
	echo "Format is ./crtusr <name> <pwd>, you did not specify a name."
	exit 2
fi
if [ -z "$2"]; then
	echo "Format is ./crtusr <name> <pwd>, you did not specify a pwd."
	exit 2
fi


adduser --gecos "" --disabled-password $1
chpasswd <<<"$1:$2"
usermod -aG sudo $1
rsync --archive --chown=$1:$1 ~/.ssh /home/$1
chown -R $1:$1 /home/$1
chown -R $1:$1 /var/www
ls /home/$1/.ssh

