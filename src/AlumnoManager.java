import java.io.*;

public class AlumnoManager {
    private static final String ARCHIVO_ALUMNOS = "alumnos.txt";
    private static final String ARCHIVO_ACREDITADOS = "acreditados.txt";
    private static final String ARCHIVO_NO_ACREDITADOS = "no_acreditados.txt";

    public AlumnoManager() {
        crearArchivoSiNoExiste(ARCHIVO_ALUMNOS);
        crearArchivoSiNoExiste(ARCHIVO_ACREDITADOS);
        crearArchivoSiNoExiste(ARCHIVO_NO_ACREDITADOS);
    }

    private void crearArchivoSiNoExiste(String nombreArchivo) {
        File file = new File(nombreArchivo);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error al crear archivo: " + nombreArchivo);
        }
    }

    // Ya no se usa leerAlumnos ni leerAlumnosDeArchivo

    public void agregarAlumno(Alumno alumno) {
        // Verificar unicidad de noControl
        if (buscarAlumnoPorNoControl(alumno.getNoControl()) != null) {
            System.out.println("[!] El No. Control ya existe. No se puede agregar.");
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ALUMNOS, true))) {
            bw.write(alumno.toString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error al agregar alumno.");
        }
        // Guardar en acreditados o no acreditados según promedio
        if (alumno.getPromedio() >= 7.0) {
            escribirAlumnoEnArchivo(alumno, ARCHIVO_ACREDITADOS);
        } else {
            escribirAlumnoEnArchivo(alumno, ARCHIVO_NO_ACREDITADOS);
        }
    }

    private void escribirAlumnoEnArchivo(Alumno alumno, String archivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(alumno.toString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error al escribir en archivo: " + archivo);
        }
    }

    public void editarAlumno(String noControl, Alumno nuevoAlumno) {
        File inputFile = new File(ARCHIVO_ALUMNOS);
        File tempFile = new File("alumnos_temp.txt");
        boolean encontrado = false;
        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Alumno a = Alumno.fromString(line);
                if (a != null && a.getNoControl().equals(noControl)) {
                    writer.write(nuevoAlumno.toString());
                    encontrado = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al editar alumno.");
            return;
        }
        if (!encontrado) {
            System.out.println("\n[!] Alumno no encontrado.\n");
            tempFile.delete();
            return;
        }
        inputFile.delete();
        tempFile.renameTo(inputFile);
        actualizarAcreditadosNoAcreditados();
        System.out.println("\nAlumno editado correctamente.\n");
    }

    public void eliminarAlumno(String noControl) {
        File inputFile = new File(ARCHIVO_ALUMNOS);
        File tempFile = new File("alumnos_temp.txt");
        boolean eliminado = false;
        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Alumno a = Alumno.fromString(line);
                if (a != null && a.getNoControl().equals(noControl)) {
                    eliminado = true;
                    continue; // no escribir el eliminado
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al eliminar alumno.");
            return;
        }
        if (!eliminado) {
            System.out.println("\n[!] Alumno no encontrado.\n");
            tempFile.delete();
            return;
        }
        inputFile.delete();
        tempFile.renameTo(inputFile);
        actualizarAcreditadosNoAcreditados();
        System.out.println("\nAlumno eliminado correctamente.\n");
    }

    // Ya no se usa escribirAlumnosEnArchivo(List<Alumno>, String)

    private void actualizarAcreditadosNoAcreditados() {
        // Reescribir acreditados y no acreditados desde alumnos.txt
        try (
                BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_ALUMNOS));
                BufferedWriter bwA = new BufferedWriter(new FileWriter(ARCHIVO_ACREDITADOS));
                BufferedWriter bwN = new BufferedWriter(new FileWriter(ARCHIVO_NO_ACREDITADOS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Alumno a = Alumno.fromString(line);
                if (a != null) {
                    if (a.getPromedio() >= 7.0) {
                        bwA.write(a.toString());
                        bwA.newLine();
                    } else {
                        bwN.write(a.toString());
                        bwN.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al actualizar acreditados/no acreditados.");
        }
    }

    public void mostrarAlumnos() {
        boolean hayAlumnos = false;
        String formato = "| %-12s | %-30s | %-8s | %-8s |%n";
        String separador = "+--------------+--------------------------------+----------+----------+";
        System.out.println();
        System.out.println(separador);
        System.out.printf(formato, "No. Control", "Nombre Completo", "Semestre", "Promedio");
        System.out.println(separador);
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ALUMNOS))) {
            String line;
            while ((line = br.readLine()) != null) {
                Alumno a = Alumno.fromString(line);
                if (a != null) {
                    hayAlumnos = true;
                    System.out.printf(formato, a.getNoControl(), a.getNombreCompleto(), a.getSemestre(),
                            String.format("%.2f", a.getPromedio()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo de alumnos.");
        }
        System.out.println(separador + "\n");
        if (!hayAlumnos) {
            System.out.println("No hay alumnos registrados.\n");
        }
    }

    // Buscar alumno por noControl (devuelve Alumno o null)
    public Alumno buscarAlumnoPorNoControl(String noControl) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ALUMNOS))) {
            String line;
            while ((line = br.readLine()) != null) {
                Alumno a = Alumno.fromString(line);
                if (a != null && a.getNoControl().equals(noControl)) {
                    return a;
                }
            }
        } catch (IOException e) {
            // ignorar
        }
        return null;
    }
}
