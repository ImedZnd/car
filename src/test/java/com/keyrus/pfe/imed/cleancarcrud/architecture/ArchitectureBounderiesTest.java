package com.keyrus.pfe.imed.cleancarcrud.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArchitectureBounderiesTest {

    @Test
    @DisplayName("clean world packages must be pure")
    void clean_world_packages_must_be_pure() {
        ArchRuleDefinition
                .noClasses()
                .that()
                .resideInAPackage("com.keyrus.pfe.imed.cleancarcrud.cleanworld..")
                .should()
                .dependOnClassesThat()
                .resideOutsideOfPackages(
                        "com.keyrus.pfe.imed.cleancarcrud.cleanworld..",
                        "java..",
                        "javax..",
                        "io.vavr..",
                        "org.junit.."
                )
                .check(new ClassFileImporter().importPackages(".."));
    }

}