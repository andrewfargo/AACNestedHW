import static java.lang.reflect.Array.newInstance;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;

/**
 * A basic implementation of Associative Arrays with keys of type K
 * and values of type V. Associative Arrays store key/value pairs
 * and permit you to look up values by key.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @author Andrew N. Fargo
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> implements Iterable<K> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V>[] pairs;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings({ "unchecked" })
  public AssociativeArray() {
    // Creating new arrays is sometimes a PITN.
    this.pairs = (KVPair<K, V>[]) newInstance((new KVPair<K, V>()).getClass(),
        DEFAULT_CAPACITY);
    this.size = 0;
  } // AssociativeArray()

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   *
   * @return a new copy of the array
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> ret = new AssociativeArray<K, V>();
    for (int i = 0; i < this.size(); i++) {
      KVPair<K, V> current = this.pairs[i];
      try {
        ret.set(current.key, current.val);
      } catch (NullKeyException e) {
        // This really shouldn't happen
        throw new RuntimeException("Nullified Key");
      } // try/catch
    } // for i
    return ret;
  } // clone()

  /**
   * Convert the array to a string.
   *
   * @return a string of the form "{Key0:Value0, Key1:Value1, ... KeyN:ValueN}"
   */
  public String toString() {
    String ret = "{";
    for (int i = 0; i < this.size(); i++) {
      KVPair<K, V> pair = this.pairs[i];
      ret += pair.toString() + ", ";
    } // for
    ret = ret.substring(0, ret.length() - 2); // remove the last comma
    ret += "}";
    return ret;
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to
   * get(key) will return value.
   *
   * @param key
   *   The key whose value we are setting.
   * @param value
   *   The value of that key.
   *
   * @throws NullKeyException
   *   If the client provides a null key.
   */
  public void set(K key, V value) throws NullKeyException {
    if (key == null) {
      throw new NullKeyException();
    } // if
    try {
      int idx = this.find(key);
      this.pairs[idx].val = value;
    } catch (KeyNotFoundException e) {
      if (this.size() == this.pairs.length) {
        this.expand();
      } // if
      this.pairs[this.size++] = new KVPair<K, V>(key, value);
    } // try/catch
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @param key
   *   A key
   * @return The corresponding value.
   * @throws KeyNotFoundException
   *   when the key is null or does not appear in the associative array.
   */
  public V get(K key) throws KeyNotFoundException {
    return this.pairs[this.find(key)].val;
  } // get(K)

  /**
   * Determine if key appears in the associative array. Should
   * return false for the null key.
   *
   * @param key
   *   The key to test.
   * @return true if the key is present in the list,
   *   false otherwise.
   */
  public boolean hasKey(K key) {
    try {
      this.find(key);
      return true;
    } catch (KeyNotFoundException e) {
      return false;
    } // try/catch
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls
   * to get(key) will throw an exception. If the key does not appear
   * in the associative array, does nothing.
   *
   * @param key
   *   The key to remove.
   */
  public void remove(K key) {
    try {
      int idx = this.find(key);
      this.pairs[idx] = null;
      if (idx == this.size - 1) {
        this.size--;
        return;
      } // if
      int lastIdx = size - 1;
      this.pairs[idx] = this.pairs[lastIdx];
      this.pairs[lastIdx] = null;
      this.size--;
    } catch (KeyNotFoundException e) {
      return;
    } // try/catch
  } // remove(K)

  /**
   * Determine how many key/value pairs are in the associative array.
   *
   * @return The pair count as an integer.
   */
  public int size() {
    return this.size;
  } // size()

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Expand the underlying array.
   */
  void expand() {
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key.
   * If no such entry is found, throws an exception.
   *
   * @param key
   *   The key of the entry.
   * @return The index into the pair array at which `key` is located.
   * @throws KeyNotFoundException
   *   If the key does not appear in the associative array.
   */
  int find(K key) throws KeyNotFoundException {
    for (int i = 0; i < this.size(); i++) {
      if (this.pairs[i].key.equals(key)) {
        return i;
      } // if
    } // for
    throw new KeyNotFoundException();
  } // find(K)

  /**
   * Create an iterator of every key value in the list.
   * @return An Iterator of every key in the mapping.
   */
  public Iterator<K> iterator() {
    return new Iterator<K>() {
      int i = 0;
      public K next() throws NoSuchElementException {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        }
        return AssociativeArray.this.pairs[i].key;
      }

      public boolean hasNext() {
        return this.i < AssociativeArray.this.size;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
} // class AssociativeArray

