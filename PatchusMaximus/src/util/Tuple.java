package util;

public class Tuple<T, K> {

	private T c1;
	private K c2;

	public Tuple(T t, K k) {
		this.setC1(t);
		this.setC2(k);
	}

	public boolean equals(Tuple<? extends Object, ? extends Object> t2) {
		return t2.getC1().equals(this.c1) && t2.getC2().equals(this.c2);
	}

	/**
	 * @return the c1
	 */
	public T getC1() {
		return c1;
	}

	/**
	 * @param c1
	 *            the c1 to set
	 */
	public void setC1(T c1) {
		this.c1 = c1;
	}

	/**
	 * @return the c2
	 */
	public K getC2() {
		return c2;
	}

	/**
	 * @param c2
	 *            the c2 to set
	 */
	public void setC2(K c2) {
		this.c2 = c2;
	}
}
