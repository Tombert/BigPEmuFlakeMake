(ns bigpfetch.core
  (:import [org.jsoup.select Elements ]
           [org.jsoup Jsoup]
           )
  (:use [clojure.java.shell :only [sh]] [selmer.parser])
  (:gen-class))

(def license "
  BigPEmu Copyright (C) 2022 Rich Whitehouse

As with all of the free software that I write, I accept no liability and offer no warranty. By using this software, you agree that you are solely responsible for any form of harm which may result from the use of this software.

I forbid the use of this software for any form of private financial gain. You may not distribute this software without the inclusion of this document in unmodified form, and you may not distribute modified versions of the main BigPEmu executable file. Any exemption from these terms requires my written permission.

For a list of third party software included in BigPEmu and the corresponding license/copyright information, please see the Data/ThirdParty/Licenses directory. (location may vary per platform/distribution, check the application documents folder on iOS/macOS) I've also auto-converted and included a few shaders written by Hyllian under the MIT license, see the relevant shader files in Data/ScreenEffects for the original license/copyright information.
  ")

(defn get-bigpemu-url []
  (let [doc (->> "https://www.richwhitehouse.com/jaguar/index.php?content=download" 
                 (Jsoup/connect) 
                 (.get))]

    (-> doc 
        (.select "strong") 
        (.last)
        (.nextElementSibling) 
        (.attr "href")
        (clojure.string/trim)
        )))

(def flake-template 
 "
{
  description = \"A flake for the bigpemu\";

  inputs = {
    nixpkgs.url = \"github:NixOS/nixpkgs\";
  };

  outputs = { self, nixpkgs }:
    let
      system = \"x86_64-linux\";
      pkgs = import nixpkgs { inherit system; config.allowUnfree = true;};
      fullPath = pkgs.lib.makeLibraryPath (with pkgs; [
	SDL2
	SDL
	glui
	libGLU
	libGL
      ]);
    in
    {
      packages.${system}.default = pkgs.buildFHSEnv {
        name = \"bigpemu-fhs\";
        targetPkgs = pkgs: [
          (pkgs.stdenv.mkDerivation rec {

            pname = \"bigpemu\";
            name = \"bigpemu\";
            src = pkgs.fetchurl {
              url = \"{{url}}\";
              sha256 = \"{{hash}}\";
            };

            nativeBuildInputs = [ pkgs.makeWrapper ];
            buildInputs = [ pkgs.dpkg ];

            unpackPhase = ''
	    tar -xvf ${src} 
	    '';

            installPhase = ''
              mkdir -p $out/bin

	      cp -r bigpemu/* $out/bin
	      wrapProgram $out/bin/bigpemu \\
                --prefix LD_LIBRARY_PATH : $out/bin/bigpemu:${fullPath} 

            '';

            dontPatchELF = true;

            meta = {
              description = \"Atari Jaguar Emulator\";
              homepage = \"https://www.richwhitehouse.com/jaguar/index.php\";
              license = pkgs.lib.licenses.unfree;
              maintainers = with pkgs.lib.maintainers; [
                tombert
              ];
              platforms = [ \"x86_64-linux\" ];
            };
          })
        ];
        runScript = \"bigpemu\";
      };

      apps.${system}.bigp = {
        type = \"app\";
        program = \"${self.packages.${system}.default}/bin/bigpemu\";
      };
    };
}")

(defn get-hash [url] 
  (let [init-hash (clojure.string/trim (:out (sh "nix-prefetch-url" url)))]
    (clojure.string/trim (:out (sh "nix" "hash" "to-sri" "--type" "sha256" init-hash)))))

(defn -main
  [& args]
  (let [big-p-url (get-bigpemu-url)
        big-p-hash (get-hash big-p-url)
        rendered (render flake-template {:url big-p-url :hash big-p-hash})
        ]
    (spit "flake.nix" rendered)
    (spit "BigPEmuLicense.txt" license)
    (System/exit 0)))
