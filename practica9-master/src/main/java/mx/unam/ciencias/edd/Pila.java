package mx.unam.ciencias.edd;

/**
 * Clase para pilas genéricas.
 */
public class Pila<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la pila.
     * @return una representación en cadena de la pila.
     */
    @Override public String toString()
    {
        String s = "";
        if (this.esVacia())
        	return s;
        Nodo aux = this.cabeza;
        while (aux != null)
        {
            s += aux.elemento + "\n";
            aux = aux.siguiente;
        }
        return s;
    }

    /**
     * Agrega un elemento al tope de la pila.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento)
    {
        if (elemento == null)
        throw new IllegalArgumentException();
        Nodo nuevo = new Nodo(elemento);
        if (this.esVacia())
        {
            this.rabo = nuevo;
        }
        else
        {
            nuevo.siguiente = this.cabeza;
        }
        this.cabeza = nuevo;
    }
}
