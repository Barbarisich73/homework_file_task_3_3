import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class GameProgress implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static List<String> namesOfSavedFiles = new ArrayList<>();
    static GameProgress gameProgress;

    private int heals;
    private int weapons;
    private int lvl;
    private double distance;

    public GameProgress(int heals, int weapons, int lvl, double distance) {
        this.heals = heals;
        this.weapons = weapons;
        this.lvl = lvl;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "GameProgress: " +
                "heals=" + heals +
                ", weapons=" + weapons +
                ", lvl=" + lvl +
                ", distance=" + distance;
    }

    public static void saveGame(String way, GameProgress save) {
        try (FileOutputStream fos = new FileOutputStream(way);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(save);
            namesOfSavedFiles.add(way);
            System.out.println("Игра сохранена: " + "\"" + way + "\"");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(String way, List<String> names) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(way))) {
            for (String name : names) {
                try (FileInputStream fis = new FileInputStream(name)) {
                    ZipEntry entry = new ZipEntry(name);
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                }
            }
            System.out.println("Создан архив: " + "\"" + way + "\"");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void deleteFile(List<String> names) {
        for (String name : names) {
            File file = new File(name);
            if (file.delete()) {
                System.out.println("Удален файл " + "\"" + name + "\"");
            } else {
                System.out.println("Файл " + "\"" + name + "\"" + " не может быть удален");
            }
        }

    }

    static void openZip(String wayToZip, String wayToFile) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(wayToZip))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
            System.out.println("Архив: " + "\"" + wayToZip + "\"" + " распакован в папку - " + "\"" + wayToFile + "\"");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void openProgress(String way) {
        try (FileInputStream fis = new FileInputStream(way);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage(
            ));
        }
        System.out.println(gameProgress);
    }

    public static void main(String[] args) {
        GameProgress save1 = new GameProgress(5, 32, 4, 56.6);
        GameProgress save2 = new GameProgress(4, 25, 8, 156.6);
        GameProgress save3 = new GameProgress(12, 20, 2, 256.6);

        saveGame("Games\\savegames\\save1.dat", save1);
        saveGame("Games\\savegames\\save2.dat", save2);
        saveGame("Games\\savegames\\save3.dat", save3);

        zipFiles("Games\\savegames\\save.zip", namesOfSavedFiles);

        deleteFile(namesOfSavedFiles);

        openZip("Games\\savegames\\save.zip", "Games\\savegames\\");

        openProgress("Games\\savegames\\save3.dat");

    }
}


