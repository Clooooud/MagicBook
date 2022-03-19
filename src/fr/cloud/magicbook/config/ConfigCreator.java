package fr.cloud.magicbook.config;

import fr.cloud.magicbook.MagicBook;
import fr.cloud.magicbook.books.Book;
import fr.cloud.magicbook.books.callables.BookCallable;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigCreator {

    private final MagicBook plugin;
    private final File file;
    private FileConfiguration configuration;

    private Map<Book, List<Field>> configurableFields;

    public ConfigCreator(MagicBook plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "book.yml");
    }

    public void load() {
        configurableFields = getConfigurableFields();
        createFile();
        setDefaults();

        configurableFields.forEach(((book, fields) -> fields.forEach(field -> {
            try {
                if (BookCallable.class.isAssignableFrom(field.getDeclaringClass())) {
                    field.set(book.getSpell(), configuration.get(book.getRegistryName() + "." + field.getName()));
                } else {
                    field.set(book, configuration.get(book.getRegistryName() + "." + field.getName()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        })));

        configuration.options().copyDefaults(true);
        save();
    }

    public void save() {
        try {
            PrintStream bufferedWriter = new PrintStream(new FileOutputStream(file), true, "UTF8");
            String csq = configuration.saveToString();
            bufferedWriter.append(csq);
            bufferedWriter.close();
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createFile() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    private void setDefaults() {
        configurableFields.forEach(((book, fields) -> fields.forEach(field -> {
            try {
                if (BookCallable.class.isAssignableFrom(field.getDeclaringClass())) {
                    configuration.addDefault(book.getRegistryName() + "." + field.getName(), field.get(book.getSpell()));
                } else {
                    configuration.addDefault(book.getRegistryName() + "." + field.getName(), field.get(book));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        })));
    }

    private Map<Book, List<Field>> getConfigurableFields() {
        Map<Book, List<Field>> fields = new HashMap<>();
        Book.getBookSet().forEach(book -> {
            List<Field> fieldList = new ArrayList<>();
            for (Field declaredField : book.getClass().getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Parameter.class)) {
                    declaredField.setAccessible(true);
                    fieldList.add(declaredField);
                }
            }
            for (Field declaredField : book.getSpell().getClass().getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Parameter.class)) {
                    declaredField.setAccessible(true);
                    fieldList.add(declaredField);
                }
            }
            fields.put(book, fieldList);
        });
        return fields;
    }
}
