package dev.sirlennox.minecrafttexturepacksoundindexer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MinecraftTexturepackSoundIndexer {

    public static void main(String[] args) {
        if (args.length < 1)
            throw new IllegalStateException("Usage: <Path> [Namespace]");

        File dir = new File(args[0]);
        final String namespace = args.length >= 2 ? args[1] : null;


        if (!dir.exists())
            throw new IllegalStateException("File does not exist!");

        if (!dir.isDirectory())
            throw new IllegalStateException("File is not a directory!");


        MinecraftTexturepackSoundIndexer mtsi = new MinecraftTexturepackSoundIndexer(dir, ".ogg", namespace);

        System.out.println(mtsi.toJson());
    }


    private final File file;
    private final String fileExtension;
    private final String namespace;


    public MinecraftTexturepackSoundIndexer(final File file, final String fileExtension, final String namespace) {
        this.file = file;
        this.fileExtension = fileExtension;
        this.namespace = namespace;
    }


    private void index(File dir, List<Sound> sounds, List<String> path) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                List<String> path2 = new LinkedList<>();
                path2.addAll(path);
                path2.add(f.getName());
                this.index(f, sounds, path2);
            } else if (f.getName().endsWith(this.getFileExtension())) {
               sounds.add(new Sound(f.getName(), path));
            }
        }
    }

    public JsonObject toJson() {

        JsonObject json = new JsonObject();
        List<Sound> sounds = this.index();

        for (Sound s : sounds) {
            JsonObject soundObj = new JsonObject();
            JsonArray soundsArray = new JsonArray();
            soundsArray.add(String.format("%s%s", namespace != null ? String.format("%s:", namespace) : "", s.toString("/")));
            soundObj.add("sounds", soundsArray);
            json.add(s.toString("."), soundObj);
        }

        return json;
    }



    public List<Sound> index() {
        List<Sound> sounds = new LinkedList<>();
        this.index(this.getFile(), sounds, new LinkedList<>());
        return sounds;
    }

    public File getFile() {
        return this.file;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public static class Sound {
        private final String name;
        private final List<String> path;

        public Sound(final String name, final List<String> path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return this.name;
        }

        public List<String> getPath() {
            return this.path;
        }

        public String toString(String split) {
            return String.format("%s%s%s", String.join(split, this.getPath()), split, this.getName().split("\\.")[0]);
        }
    }
 }
