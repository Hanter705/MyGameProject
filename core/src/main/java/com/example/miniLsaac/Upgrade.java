package com.example.miniLsaac;

/**
 * Representa una mejora (Upgrade) que el jugador puede seleccionar al subir de nivel.
 * <p>
 * Cada mejora tiene un nombre, una descripción y un efecto asociado que se ejecuta
 * al seleccionarla. El efecto se define mediante un objeto {@link Runnable},
 * lo que permite aplicar distintas acciones (como aumentar daño, velocidad, etc.)
 * de forma flexible.
 * </p>
 */
public class Upgrade {

    /** Nombre de la mejora (por ejemplo: "Daño +20%"). */
    private final String name;

    /** Breve descripción de lo que hace la mejora. */
    private final String description;

    /** Acción que se ejecutará al aplicar la mejora. */
    private final Runnable effect;

    /**
     * Crea una nueva mejora.
     *
     * @param name nombre de la mejora
     * @param description descripción breve del efecto que produce
     * @param effect acción que se ejecutará cuando el jugador la seleccione
     */
    public Upgrade(String name, String description, Runnable effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;
    }

    /**
     * Devuelve el nombre de la mejora.
     *
     * @return nombre de la mejora
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve la descripción de la mejora.
     *
     * @return descripción breve del efecto
     */
    public String getDescription() {
        return description;
    }

    /**
     * Aplica la mejora ejecutando su acción asociada.
     * <p>
     * Internamente llama al método {@code run()} del {@link Runnable} almacenado.
     * </p>
     */
    public void apply() {
        effect.run(); // 🪄 Ejecuta el efecto (por ejemplo: aumenta daño, velocidad, etc.)
    }
}
