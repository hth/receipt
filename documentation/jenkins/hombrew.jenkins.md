Install Homebrew on Build machine
=================================

Homebrew installation requires Admin privileges. For this to work, log in as `maude`
Copy the URL to install Homebrew on command line
Once installation is complete run `brew doctor`
After this try to change the `chown` for `Jenkins` user
Command to change ownership within `local` directory
    sudo chown -R jenkins:wheel share

After that, change ownership of the folder `local`     
