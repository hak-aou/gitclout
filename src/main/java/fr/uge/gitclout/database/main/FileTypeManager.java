package fr.uge.gitclout.database.main;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileTypeManager {

    private final Properties properties = new Properties();

    private Set<String> codes_set;
    private Set<String> builds_set;
    private Set<String> config_set;
    private Set<String> resources_set;
    private Set<String> doc_set;

    public FileTypeManager() {
        try {
            // Read the configuration file to get values
            properties.load(FileTypeManager.class.getClassLoader().getResourceAsStream("META-INF/type_configuration"));
            readConfigFileValues();
        } catch (IOException e) {
            // If we can't get the configurations file, we have these default values.
            defaultValues();
        }
    }

    /**
     * Function to initialize Set with values read in the configuration file
     */
    private void readConfigFileValues() {
        codes_set     = getValueConfig("codes");
        builds_set    = getValueConfig("builds");
        config_set    = getValueConfig("configurations");
        resources_set = getValueConfig("resources");
        doc_set       = getValueConfig("documentations");
    }

    /**
     * Function to initialize Set if we can't read the configuration file
     */
    private void defaultValues() {
        codes_set     = Set.of("c", "js", "java", "py");
        builds_set    = Set.of("xml");
        config_set    = Set.of("github", "gitignore");
        resources_set = Set.of("txt", "png", "jpg", "mp4");
        doc_set       = Set.of("md");
    }

    /**
     * Get the values of the key
     * @param key key to retrieve the values
     * @return Set of file extensions
     */
    public Set<String> getValueConfig(String key) {
        return Arrays.stream(properties.getProperty(key).split(","))
                                                        .collect(Collectors.toSet());
    }


    /**
     * Function to determine the file type of the given file
     * @param file File object
     * @return The file type of the given file
     */
    public FileType type(File file) {
        String extension = getExtension(file);

        if(codes_set.contains(extension))          return FileType.CODE;
        else if(builds_set.contains(extension))    return FileType.BUILD;
        else if(config_set.contains(extension))    return FileType.CONFIGURATION;
        else if(resources_set.contains(extension)) return FileType.RESSOURCE;
        else if(doc_set.contains((extension)))     return FileType.DOCUMENTATION;
        return null;
    }

    /**
     * Return all the extensions file
     * @return List of all extensions
     */
    public List<String> listAllExtensions() {
        return Stream.of(codes_set, builds_set, codes_set, resources_set, doc_set)
                .flatMap(Set::stream)
                .toList();
    }

    /**
     * Retrive only the extension of file
     * @param file File object
     * @return extension of file
     */
    public String getExtension(File file) {
        /* Get the file in the path then get the extension */
        var splitFileName = file.toString().split("\\.");
        return splitFileName[splitFileName.length - 1];
    }
}
