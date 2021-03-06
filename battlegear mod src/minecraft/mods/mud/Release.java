package mods.mud;

import java.util.Arrays;

public class Release implements Comparable<Release>{
    public final int[] version;
    public final EnumReleaseType type;
    public String url;
    public String download;
    public String md5;

    public Release(EnumReleaseType type, String url, int[] version, String download) {
        this(type, url, version, download, null);
    }

    public Release(EnumReleaseType type, String url, int[] version, String download, String md5) {
        this.type = type;
        this.url = url;
        this.version = version;
        this.download = download;
        this.md5 = md5;
    }

    @Override
    public int compareTo(Release other) {

        for(int i = 0; i < version.length && i < other.version.length; i++){
            if(version[i] > other.version[i]){
                return 1;
            }else if (version[i] < other.version[i]){
                return -1;
            }
        }

        return version.length - other.version.length;
    }

    public String getVersionString(){
        StringBuilder newVersionString = new StringBuilder();
        for (int i : version) {
            newVersionString.append(i).append(".");
        }
        newVersionString.deleteCharAt(newVersionString.lastIndexOf("."));
        return newVersionString.toString();
    }

    @Override
    public String toString() {
        return "Release{type=" + type +
                ", version=" + Arrays.toString(version) +
                ", url='" + url + '\'' + '}';
    }


    public enum EnumReleaseType{
        Normal(1),
        Beta(2),
        Dev(3);

        public int level;

        EnumReleaseType(int level){
            this.level = level;
        }
    }
}
