public class Alumno {
    private String noControl;
    private String nombreCompleto;
    private int semestre;
    private double promedio;

    public Alumno(String noControl, String nombreCompleto, int semestre, double promedio) {
        this.noControl = noControl;
        this.nombreCompleto = nombreCompleto;
        this.semestre = semestre;
        this.promedio = promedio;
    }

    public String getNoControl() {
        return noControl;
    }

    public void setNoControl(String noControl) {
        this.noControl = noControl;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public double getPromedio() {
        return promedio;
    }

    public void setPromedio(double promedio) {
        this.promedio = promedio;
    }

    @Override
    public String toString() {
        return noControl + "," + nombreCompleto + "," + semestre + "," + promedio;
    }

    public static Alumno fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4)
            return null;
        try {
            String noControl = parts[0];
            String nombreCompleto = parts[1];
            int semestre = Integer.parseInt(parts[2]);
            double promedio = Double.parseDouble(parts[3]);
            return new Alumno(noControl, nombreCompleto, semestre, promedio);
        } catch (Exception e) {
            return null;
        }
    }
}
