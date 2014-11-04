Install Homebrew on Build machine
=================================

- Homebrew installation requires Admin privileges. For this to work, log in as `maude` (maude is the Admin)
- Copy the URL to install Homebrew on command line
- Once installation is complete run `brew doctor`
- After this try to change the ownership of folder using `chown` for `Jenkins` user
- Command to change ownership within `local` directory
    
        sudo chown -R jenkins:wheel share
- After that, change ownership of the folder `/usr/local` 
    
        sudo chown jenkins:wheel local
- Now you are set to run command on jenkins who is still a standard user
- Then change `/Library/Caches/Homebrew` to `jenkins:wheel` ownership because `homebrew` cannot write formulas to `/Library/Caches/Homebrew/Formulas` without correct permissions
