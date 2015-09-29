package util;

import java.util.ArrayList;

import skyproc.ARMO;
import skyproc.FormID;
import skyproc.WEAP;

public class Bucket<T, K> {
	private T key;
	private ArrayList<K> bindings;

	public Bucket(T t, ArrayList<K> k) {
		this.key = t;
		this.bindings = k;
	}

	/**
	 * @return the key
	 */
	public T getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(T key) {
		this.key = key;
	}

	/**
	 * @return the bindings
	 */
	public ArrayList<K> getBindings() {
		return bindings;
	}

	/**
	 * @param bindings
	 *            the bindings to set
	 */
	public void setBindings(ArrayList<K> bindings) {
		this.bindings = bindings;
	}

	public String toString() {
		return "key: " + this.key.toString() + " | values: "
				+ this.getBindings().toString();
	}

	// I hate Java's type system sometimes
	public static boolean doesBucketListContainKey(
			ArrayList<Bucket<? extends Object, ? extends Object>> l, Object k) {
		for (Bucket<? extends Object, ? extends Object> t : l) {
			if (t.getKey().equals(k)) {
				return true;
			}
		}
		return false;
	}

	public static boolean doesBucketListContainKey(
			ArrayList<Bucket<WEAP, WEAP>> l, WEAP k) {
		for (Bucket<WEAP, WEAP> t : l) {
			if (t.getKey().equals(k)) {
				return true;
			}
		}
		return false;
	}

	public static boolean doesBucketListContainKey(
			ArrayList<Bucket<ARMO, ARMO>> l, ARMO k) {
		for (Bucket<ARMO, ARMO> t : l) {
			if (t.getKey().equals(k)) {
				return true;
			}
		}
		return false;
	}

	public static boolean doesBucketListContainKey(
			ArrayList<Bucket<FormID, FormID>> l, FormID k) {
		for (Bucket<FormID, FormID> t : l) {
			if (t.getKey().equals(k)) {
				return true;
			}
		}
		return false;
	}

	public static boolean doesBucketListContainKey(
			ArrayList<Bucket<String, String>> l, String k) {
		for (Bucket<String, String> t : l) {
			if (t.getKey().equals(k)) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<? extends Object> getBindingsFromListByKey(
			ArrayList<Bucket<Object, Object>> l, Object k) {
		for (Bucket<? extends Object, ? extends Object> t : l) {
			if (t.getKey().equals(k)) {
				return t.getBindings();
			}
		}
		return null;
	}

	public static ArrayList<WEAP> getBindingsFromListByKey(
			ArrayList<Bucket<WEAP, WEAP>> l, WEAP k) {
		for (Bucket<WEAP, WEAP> t : l) {
			if (t.getKey().equals(k)) {
				return t.getBindings();
			}
		}
		return null;
	}

	public static ArrayList<ARMO> getBindingsFromListByKey(
			ArrayList<Bucket<ARMO, ARMO>> l, ARMO k) {
		for (Bucket<ARMO, ARMO> t : l) {
			if (t.getKey().equals(k)) {
				return t.getBindings();
			}
		}
		return null;
	}

	public static void addToBucketWithKey(ArrayList<Bucket<String, String>> l,
			String k, String newEntry) {
		for (Bucket<String, String> t : l) {
			if (t.getKey().equals(k)) {
				t.getBindings().add(newEntry);
			}
		}
	}

	public static Bucket<String, String> getBucketWithKeyFromList(
			ArrayList<Bucket<String, String>> l, String k) {
		for (Bucket<String, String> t : l) {
			if (t.getKey().equals(k)) {
				return t;
			}
		}
		return null;
	}

	public static Bucket<WEAP, WEAP> getBucketWithKeyFromList(
			ArrayList<Bucket<WEAP, WEAP>> l, WEAP k) {
		for (Bucket<WEAP, WEAP> t : l) {
			if (t.getKey().equals(k)) {
				return t;
			}
		}
		return null;
	}
}
