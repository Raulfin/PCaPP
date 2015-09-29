package util;

public class Triple<T, K, I> {

	private T c1;
	private K c2;
	private I c3;

	public Triple(T t, K k, I i) {
		this.setC1(t);
		this.setC2(k);
		this.setC3(i);
	}

	public boolean equals(Triple<? extends Object, ? extends Object, ? extends Object> t2) {
		return t2.getC1().equals(this.c1) && t2.getC2().equals(this.c2) && t2.getC3().equals(this.c3);
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

	/**
	 * @return the c3
	 */
	public I getC3() {
		return c3;
	}

	/**
	 * @param c3 the c3 to set
	 */
	public void setC3(I c3) {
		this.c3 = c3;
	}
}
