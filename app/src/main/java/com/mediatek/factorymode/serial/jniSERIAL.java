package com.mediatek.factorymode.serial;
public class jniSERIAL{
	private static final String TAG = "jniSERIAL";
	public static final int MAX_WRITE_LENGTH=259;//最大写入字节数，升级时最大
	/**
	 * 读数据缓存，serial_read()接口返回的数据保存在这里，调用serial_read之后从这里读数据
	 * 注意：一个int保存一个byte数据，也就是说int[32]实际保存的是32个byte，而不是32*4=128 bytes
	 */
	public int [] rd_data = new int[259];
	/**
	 * 写数据缓存, 调用serial_write()之前先把数据写入这里
	 * 注意：一个int保存一个byte数据，也就是说int[32]实际保存的是32个byte，而不是32*4=128 bytes
	 */
	public int [] wr_data = new int[259];

	/**S
	 * 调试开关，调试设备没有串口so的时候这个开关打开
	 */
	public static boolean no_serial_lib = false;

	/**
	 * 初始化串口设备
	 * @param param: jniSERIAl对象实例
	 * @param new_baudrate: 波特率
	 * @param length: 字长bit数，8位数据就是8
	 * @param parity_c: 奇偶校验位，'e' = even, 'o' = old，0 = none
	 * @param stopbits：是否有停止位，1=有，0=没有
     * @return
     */
	public native int init(Object param,int new_baudrate, int length, char parity_c, int stopbits);

	/**
	 * 读数据
	 * @param param：jniSERIAL对象实例
	 * @return：实际读到字节数
     */
	public native int serial_read(Object param);

	/**
	 * 写入数据
	 * @param param：jniSERIAL对象实例
	 * @param len：写入数据长度
	 * @return：实际写入长度
     */
	public native int serial_write(Object param, int len);

	/**
	 * 关闭串口
	 * @return
     */
	public native int exit();

	static {    
		try {
			System.loadLibrary("fm_serial_jni");
			//no_serial_lib = true;

		} catch(Throwable e) {
			e.printStackTrace(System.out);
			no_serial_lib = true;
		}

	}  	
	
}

