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

    private MagicBook plugin;
    private File file;
    private FileConfiguration configuration;

    public ConfigCreator(MagicBook plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "book.yml");
    }

    @SneakyThrows
    public void load() {
        if (file.exists()) {
            configuration = YamlConfiguration.loadConfiguration(file);
            getConfigurableFields().forEach(((book, fields) -> fields.forEach(field -> {
                try {
                    if (BookCallable.class.isAssignableFrom(field.getDeclaringClass())) {
                        configuration.addDefault(book.getRegistryName() + "." + field.getName(), field.get(book.getSpell()));
                        field.set(book.getSpell(), configuration.get(book.getRegistryName() + "." + field.getName()));
                    } else {
                        configuration.addDefault(book.getRegistryName() + "." + field.getName(), field.get(book));
                        field.set(book, configuration.get(book.getRegistryName() + "." + field.getName()));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            })));

            configuration.options().copyDefaults(true);

        } else {
            file.getParentFile().mkdirs();
            file.createNewFile();
            configuration = YamlConfiguration.loadConfiguration(file);

            getConfigurableFields().forEach(((book, fields) -> {
                fields.forEach(field -> {
                    try {
                        if (BookCallable.class.isAssignableFrom(field.getDeclaringClass())) {
                            Object value = field.get(book.getSpell());
                            if (value instanceof String) {

                            }
                            configuration.set(book.getRegistryName() + "." + field.getName(), value);
                        } else {
                            Object value = field.get(book);
                            if (value instanceof String) {

                            }
                            configuration.set(book.getRegistryName() + "." + field.getName(), value);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            }));
        }

        save();
    }

    public void save() {
        try {
            PrintStream bufferedWriter = new PrintStream(new FileOutputStream(file), true, "UTF8");
            bufferedWriter.append(configuration.saveToString());
            bufferedWriter.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
