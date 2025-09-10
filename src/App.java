import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        AlumnoManager manager = new AlumnoManager();
        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("\n========================================");
            System.out.println("         GESTION DE ALUMNOS");
            System.out.println("========================================");
            System.out.println("1. Mostrar todos los alumnos");
            System.out.println("2. Mostrar alumnos acreditados");
            System.out.println("3. Mostrar alumnos no acreditados");
            System.out.println("4. Agregar alumno");
            System.out.println("5. Editar alumno");
            System.out.println("6. Eliminar alumno");
            System.out.println("7. Salir");
            System.out.print("\nSeleccione una opcion: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Ingrese un número válido: ");
                scanner.next();
            }
            opcion = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            switch (opcion) {
                case 1:
                    mostrarAlumnosPorArchivo("alumnos.txt", "Todos los alumnos");
                    break;
                case 2:
                    mostrarAlumnosPorArchivo("acreditados.txt", "Alumnos acreditados");
                    break;
                case 3:
                    mostrarAlumnosPorArchivo("no_acreditados.txt", "Alumnos no acreditados");
                    break;
                case 4:
                    agregarAlumno(manager, scanner);
                    break;
                case 5:
                    editarAlumno(manager, scanner);
                    break;
                case 6:
                    eliminarAlumno(manager, scanner);
                    break;
                case 7:
                    System.out.println("\n¡Fin del programa!\n");
                    break;
                default:
                    System.out.println("Opcion no valida.\n");
            }
        } while (opcion != 7);
        scanner.close();
    }

    private static void mostrarAlumnosPorArchivo(String archivo, String titulo) {
        String formato = "| %-12s | %-30s | %-8s | %-8s |%n";
        String separador = "+--------------+--------------------------------+----------+----------+";
        boolean hayAlumnos = false;
        System.out.println();
        System.out.println(titulo + ":");
        System.out.println(separador);
        System.out.printf(formato, "No. Control", "Nombre Completo", "Semestre", "Promedio");
        System.out.println(separador);
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
            String line;
            while ((line = br.readLine()) != null) {
                Alumno a = Alumno.fromString(line);
                if (a != null) {
                    hayAlumnos = true;
                    System.out.printf(formato, a.getNoControl(), a.getNombreCompleto(), a.getSemestre(),
                            String.format("%.2f", a.getPromedio()));
                }
            }
        } catch (Exception e) {
        }
        System.out.println(separador + "\n");
        if (!hayAlumnos) {
            System.out.println("Tabla vacia.\n");
        }
    }

    private static void agregarAlumno(AlumnoManager manager, Scanner scanner) {
        System.out.println("--- Agregar nuevo alumno ---");
        String noControl;
        while (true) {
            System.out.print("No. Control: ");
            noControl = scanner.nextLine();
            if (noControl.trim().isEmpty()) {
                System.out.println("[!] El No. Control no puede estar vacio.");
                continue;
            }
            if (manager.buscarAlumnoPorNoControl(noControl) != null) {
                System.out.println("[!] El No. Control ya existe. Ingrese uno diferente.");
            } else {
                break;
            }
        }
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();
        int semestre = leerEntero(scanner, "Semestre: ");
        double promedio = leerDouble(scanner, "Promedio: ");
        Alumno alumno = new Alumno(noControl, nombre, semestre, promedio);
        manager.agregarAlumno(alumno);
        mostrarTablaAlumno(alumno, "Alumno agregado correctamente:");
    }

    private static void editarAlumno(AlumnoManager manager, Scanner scanner) {
        System.out.println("--- Editar alumno ---");
        System.out.print("Ingrese el No. Control del alumno a editar: ");
        String noControl = scanner.nextLine();
        Alumno actual = manager.buscarAlumnoPorNoControl(noControl);
        if (actual == null) {
            System.out.println("\n[!] Alumno no encontrado.\n");
            return;
        }
        System.out.print("Nuevo nombre completo (" + actual.getNombreCompleto() + "): ");
        String nombre = scanner.nextLine();
        if (nombre.trim().isEmpty())
            nombre = actual.getNombreCompleto();
        int semestre = leerEntero(scanner, "Nuevo semestre (" + actual.getSemestre() + "): ", 1, 20,
                actual.getSemestre());
        double promedio = leerDouble(scanner, "Nuevo promedio (" + String.format("%.2f", actual.getPromedio()) + "): ",
                0.0, 10.0, actual.getPromedio());
        Alumno nuevo = new Alumno(noControl, nombre, semestre, promedio);
        manager.editarAlumno(noControl, nuevo);
        mostrarTablaAlumno(nuevo, "Alumno modificado:");
    }

    private static void eliminarAlumno(AlumnoManager manager, Scanner scanner) {
        System.out.println("--- Eliminar alumno ---");
        System.out.print("Ingrese el No. Control del alumno a eliminar: ");
        String noControl = scanner.nextLine();
        Alumno eliminado = manager.buscarAlumnoPorNoControl(noControl);
        manager.eliminarAlumno(noControl);
        if (eliminado != null) {
            mostrarTablaAlumno(eliminado, "Alumno eliminado:");
        }
    }

    private static int leerEntero(Scanner scanner, String mensaje, int min, int max, int valorActual) {
        int valor;
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                valor = valorActual;
                break;
            }
            try {
                valor = Integer.parseInt(input);
                if (valor < min || valor > max) {
                    System.out.println("[!] El valor debe estar entre " + min + " y " + max + ".");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("[!] Ingrese un numero entero valido.");
            }
        }
        return valor;
    }

    private static int leerEntero(Scanner scanner, String mensaje) {
        return leerEntero(scanner, mensaje, 1, 20, 1);
    }

    private static double leerDouble(Scanner scanner, String mensaje, double min, double max, double valorActual) {
        double valor;
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                valor = valorActual;
                break;
            }
            try {
                valor = Double.parseDouble(input);
                if (valor < min || valor > max) {
                    System.out.println("[!] El valor debe estar entre " + min + " y " + max + ".");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("[!] Ingrese un numero valido.");
            }
        }
        return valor;
    }

    private static double leerDouble(Scanner scanner, String mensaje) {
        return leerDouble(scanner, mensaje, 0.0, 10.0, 0.0);
    }

    private static void mostrarTablaAlumno(Alumno alumno, String mensaje) {
        if (alumno == null)
            return;
        String formato = "| %-12s | %-30s | %-8s | %-8s |%n";
        String separador = "+--------------+--------------------------------+----------+----------+";
        System.out.println();
        System.out.println(mensaje);
        System.out.println(separador);
        System.out.printf(formato, "No. Control", "Nombre Completo", "Semestre", "Promedio");
        System.out.println(separador);
        System.out.printf(formato, alumno.getNoControl(), alumno.getNombreCompleto(),
                alumno.getSemestre(), String.format("%.2f", alumno.getPromedio()));
        System.out.println(separador + "\n");
    }

}
