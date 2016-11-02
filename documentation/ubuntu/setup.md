### Changes to bash_profile
Load `bash_profile` changes using command

    source .bash_profile

### Sudoer
Make `db` as sudoer in file `/etc/sudoers` at the end of file. `Shift+g` gets you to the end of file.

    db  ALL=(ALL) NOPASSWD: ALL
    
After making `db` as `sudoer`, `init 0` does not work. It now takes `sudo init 0` for reboot instead.
    
### SSH
    
    sudo apt-get update
    sudo apt-get install openssh-server
    sudo ufw allow 22
    
### Update Ubuntu
Run all three commands
    
- sudo apt-get update        # Fetches the list of available updates
- sudo apt-get upgrade       # Strictly upgrades the current packages
- sudo apt-get dist-upgrade  # Installs updates (new ones)
- sudo apt autoremove        # To remove installs 

All commands 

    sudo apt-get update && sudo apt-get upgrade && sudo apt-get dist-upgrade && sudo apt autoremove
    
#### Check if port is open 
    
    netstat -plntu | grep 61616
    netstat -plntu | grep 61616
    
- Active Internet connections (servers and established)

    
    netstat -atn           # For tcp
    netstat -aun           # For udp
    netstat -atun          # For both

- Active Internet connections (only servers)

    
    netstat -plntu