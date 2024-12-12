# BigPEmuFlakeMake 

BigPEmu is a pretty neat program to emulate the Atari Jaguar.  I had trouble making it work with vanilla NixOS.  I could of course make it work with a Flake, but why do that when I can spend a lot more time writing code to automatically generate Flakes that are up to date.  I'm not crazy, you're crazy. 


## Usage

You can download a binary for Linux in the releases page.  If you want to build this yourself, you'll need Leiningen installed. 

With *just* Leiningen, you can run it with: 

```
lein run
```

Personally I prefer to use GraalVM to get native images because I think it's cooler. If you have both Leiningen and GraalVM CE installed, you can run: 

```
make
```

This will produce a file called `flakemake`. 

## Attribution

All credit for BigPEmu goes to Richard Whitehouse. Click [here](https://www.richwhitehouse.com/jaguar/index.php) for more information about it. 

## Note

While the code written in this repo, namely the stuff written in Clojure and the Makefile, are under the MIT license, this does *not* change any of the licensing for BigPEmu.  I have absolutely no say in regards to the licensing of BigPEmu, and as such usage of any of the results of the code in this repo must be done in accordance to the BigPEmu license. 

That said, you are welcome and encouraged to take the code written here and modify it if you would like to use it as a starting point for making your own Flake generator. 
