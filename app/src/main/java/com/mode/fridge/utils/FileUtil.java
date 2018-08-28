package com.mode.fridge.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;

public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final String SELECTION_INFO = FileColumns.DATA + " NOT LIKE '%/storage/emulated/0/viomi/%'";//过滤对食材图片的查找

    private static String filePath = "/sdcard/device/";//文件夹路径
    private static final String fileName = "temp.txt";


    //private static final String SELECTION_INFO = null;

    /**
     * 创建文件f,如果已存在，不操作
     *
     * @param path 文件路径
     * @throws IOException
     */
    public static void creatFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            file.createNewFile();
        }
    }

    /***
     * 建立文件路径
     * @param path
     */
    public static void creatDirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }


    /**
     * 打印f路径下所有的文件和文件夹
     *
     * @param f 文件对象
     */
    public static void printAllFile(File f) {
        //打印当前文件名
        System.out.println(f.getName());
        //是否是文件夹
        if (f.isDirectory()) {
            //获得该文件夹下所有子文件和子文件夹
            File[] f1 = f.listFiles();
            //循环处理每个对象
            int len = f1.length;
            for (int i = 0; i < len; i++) {
                //递归调用，处理每个文件对象
                printAllFile(f1[i]);
            }
        }
    }

    /**
     * 删除对象f下的所有文件和文件夹（含自身）
     *
     * @param f 文件路径
     */
    public static void deleteAll(File f) {
        if (!f.exists()) {
            LogUtils.d(TAG, " the file is not exists");
            return;
        }
        //文件
        if (f.isFile()) {
            if (f.delete())
                LogUtils.d(TAG, "delete success");
        } else { //文件夹
            //获得当前文件夹下的所有子文件和子文件夹
            File f1[] = f.listFiles();
            //循环处理每个对象
            int len = f1.length;
            for (int i = 0; i < len; i++) {
                //递归调用，处理每个文件对象
                deleteAll(f1[i]);
            }
            //删除当前文件夹
            if (f.delete())
                LogUtils.d(TAG, "delete success");
        }
    }

    /**
     * f为文件则删除自身，f为目录则删除目录下的子文件
     *
     * @param f 文件路径
     */
    public static void deleteChildFile(File f) {
        if (!f.exists()) {
            return;
        }
        //文件
        if (f.isFile()) {
            f.delete();
        } else { //文件夹
            //获得当前文件夹下的所有子文件和子文件夹
            File f1[] = f.listFiles();
            //循环处理每个对象
            int len = f1.length;
            for (int i = 0; i < len; i++) {
                //递归调用，处理每个文件对象
                deleteAll(f1[i]);
            }
        }
    }

    /**
     * 读取asset目录下文件。
     *
     * @param context
     * @param file    文件
     * @param code    字符编码 如"utf-8"
     * @return content
     */
    public static String readAssetsFile(Context context, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = context.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取asset目录下文件。
     *
     * @param context
     * @param filename 文件
     * @return 二进制文件
     */
    public static byte[] readAssetsFile(Context context, String filename) {
        try {
            InputStream ins = context.getAssets().open(filename);
            byte[] data = new byte[ins.available()];

            ins.read(data);
            ins.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    public static byte[] readFileByBytes(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("以字符为单位读取文件内容，一次读多个字节：");
            // 一次读多个字符
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charread = reader.read(tempchars)) != -1) {
                // 同样屏蔽掉\r不显示
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != '\r')) {
                    System.out.print(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == '\r') {
                            continue;
                        } else {
                            System.out.print(tempchars[i]);
                        }
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFile(String fileName) {
        String result = "";
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                line++;
                result += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return result;
    }

    /**
     * 随机读取文件内容
     */
    public static void readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        try {
            System.out.println("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 4 : 0;
            // 将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            // 将一次读取的字节数赋给byteread
            while ((byteread = randomFile.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 显示输入流中还剩的字节数
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * A方法追加文件：使用RandomAccessFile
     */
    public static void appendMethodA(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * B方法追加文件：使用FileWriter
     */
    public static void appendMethodB(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "appendMethodB fail !msg=" + e.getMessage());
            e.printStackTrace();
        }
    }

    //写文件  
    public static void writeFile(String fileName, String write_str) throws IOException {
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = write_str.getBytes();
        fos.write(bytes);
        fos.close();
    }

    /***
     * 将一个对象保存到本地(手机内部存储)
     * @param context
     * @param filename 文件名
     * @param object 对象
     */
    public static void saveObject(Context context, String filename, Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /***
     * 从本地(手机内部存储)读取保存的对象
     * @param filename 文件名称
     * @return
     */
    public static Object getObject(Context context, String filename) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(filename);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            Log.e(TAG, "getObject error,msg=" + e.getMessage());
            //e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static File uri2File(Context context, Uri uri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        File file = new File(res);
        return file;
    }

//    /**
//     * 单位 bit
//     * 支持音频、视频、图片
//     *
//     * @param mContext
//     * @param type
//     * @return
//     */
//    public static long queryFileSize(Context mContext, FileType type) {
//        Uri uri;
//        if (type == FileType.FILE_VIDEO)
//            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        else if (type == FileType.FILE_PIC)
//            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        else if (type == FileType.FILE_MUSIC)
//            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        else
//            return -1;
//        Cursor c = mContext.getContentResolver().query(uri,//数据资源路径
//                new String[]{"SUM(_size)"},//查询的列  筛选、排序时用
//                SELECTION_INFO,//null,//查询的条件
//                null/*条件填充值*/,
//                null/*排序依据*/);
//        if (c != null && c.moveToFirst()) {
//            long size = c.getLong(0);
//            LogUtils.w(TAG, "the size is:" + size);
//            c.close();
//            return size;
//        }
//        return -1;
//    }
//}
//    public static List<FileInfo> getFileInfoList(Context mContext, FileType type) {
//        List<FileInfo> list = new ArrayList<FileInfo>();
//        Cursor cursor = null;
//        if (type == FileType.FILE_MUSIC) {
//            cursor = mContext.getContentResolver().query(
//                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    new String[]{FileColumns._ID, FileColumns.DATA,
//                            FileColumns.SIZE, FileColumns.DISPLAY_NAME,
//                            FileColumns.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID}, null,
//                    null, null);
//        } else if (type == FileType.FILE_VIDEO) {
//            cursor = mContext.getContentResolver().query(
//                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                    new String[]{FileColumns._ID, FileColumns.DATA,
//                            FileColumns.SIZE, FileColumns.DISPLAY_NAME,
//                            FileColumns.TITLE, MediaStore.Video.Media.DURATION}, null,
//                    null, null);
//        } else if (type == FileType.FILE_PIC) {
//            cursor = mContext.getContentResolver().query(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    new String[]{FileColumns._ID, FileColumns.DATA,
//                            FileColumns.SIZE, FileColumns.DISPLAY_NAME,
//                            FileColumns.TITLE}, SELECTION_INFO,//null,
//                    null, null);
//        } else if (type == FileType.FILE_APK) {
//            String selection = FileColumns.DATA + " LIKE '%.apk'";
//            cursor = mContext.getContentResolver().query(
//                    MediaStore.Files.getContentUri("external"),
//                    new String[]{FileColumns._ID, FileColumns.DATA,
//                            FileColumns.SIZE, FileColumns.DISPLAY_NAME,
//                            FileColumns.TITLE}, selection,
//                    null, null);
//        } else if (type == FileType.FILE_GARBAGE) {
//            String selection = FileColumns.DATA + " LIKE '%.tmp'"
//                    + " OR " + FileColumns.DATA + " LIKE '%.temp'"
//                    + " OR " + FileColumns.DATA + " LIKE '%.apk'"
//                    + " OR " + FileColumns.DATA + " LIKE '%.log'";
//            cursor = mContext.getContentResolver().query(
//                    MediaStore.Files.getContentUri("external"),
//                    new String[]{FileColumns._ID, FileColumns.DATA,
//                            FileColumns.SIZE, FileColumns.DISPLAY_NAME,
//                            FileColumns.TITLE}, selection,
//                    null, null);
//        }
//        while (cursor != null && cursor.moveToNext()) {
//            FileInfo info = new FileInfo(type, cursor.getInt(cursor.getColumnIndex(FileColumns._ID)),
//                    cursor.getString(cursor.getColumnIndex(FileColumns.TITLE)),
//                    cursor.getString(cursor.getColumnIndex(FileColumns.DATA)),
//                    cursor.getLong(cursor.getColumnIndex(FileColumns.SIZE)));
//
//            if (type == FileType.FILE_VIDEO || type == FileType.FILE_MUSIC) {
//                info.setTimeLong(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
//            }
//
//            if (type == FileType.FILE_MUSIC)
//                info.setAlbum_id(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//            LogUtils.d(TAG, " the item is:" + info.getAbsolutePath());
//            list.add(info);
//        }
//        if (cursor != null)
//            cursor.close();
//        return list;
//    }

    //public static void deleteTarget(String path) {
    //    File target = new File(path);
    //
    //    if (target.isFile() && target.canWrite()) {//是文件
    //        LogUtils.d(TAG,"the file absolute path is:"+target.getAbsolutePath());
    //        target.delete();
    //    } else if (target.isDirectory() && target.canRead() && target.canWrite()) {//是目录
    //        String[] fileList = target.list();
    //
    //        if (fileList != null && fileList.length == 0) {//空目录
    //            target.delete();
    //            return;
    //        } else if (fileList != null && fileList.length > 0) {
    //            for (String aFile_list : fileList) {
    //                File tempF = new File(target.getAbsolutePath() + "/"
    //                        + aFile_list);
    //
    //                if (tempF.isDirectory())
    //                    deleteTarget(tempF.getAbsolutePath());
    //                else if (tempF.isFile()) {
    //                    LogUtils.d(TAG,"the file absolute path is:"+target.getAbsolutePath());
    //                    tempF.delete();
    //                }
    //            }
    //        }
    //
    //        if (target.exists()) {
    //            LogUtils.d(TAG,"the file absolute path is:"+target.getAbsolutePath());
    //            target.delete();
    //        }
    //    }
    //}

//    //清理缓存
//    public static boolean cleanAppsCache(Context mContext) {
//        //
//        File externalDir = mContext.getExternalCacheDir();
//        if (externalDir == null) {
//            return true;
//        }
//
//        PackageManager pm = mContext.getPackageManager();
//        List<ApplicationInfo> installedPackages = pm.getInstalledApplications(PackageManager.GET_GIDS);
//        for (ApplicationInfo info : installedPackages) {
//            if (info.packageName.equals("com.viomi.fridge"))
//                continue;
//            //if (packageNameList.contains(info.packageName)) {
//            String externalCacheDir = externalDir.getAbsolutePath()
//                    .replace(mContext.getPackageName(), info.packageName);
//            File externalCache = new File(externalCacheDir);
//            if (externalCache.exists() && externalCache.isDirectory()) {
//                FileUtil.deleteAll(new File(externalCacheDir));
//                //FileUtil.deleteTarget(externalCacheDir);
//            }
//            //}
//        }
//        //
//        final boolean[] isSuccess = {false};
//        try {
//            Method freeStorageAndNotify = pm.getClass()
//                    .getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
//            long freeStorageSize = Long.MAX_VALUE;
//
//            freeStorageAndNotify.invoke(pm, freeStorageSize, new IPackageDataObserver.Stub() {
//
//                @Override
//                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
//                    isSuccess[0] = succeeded;
//                }
//            });
//
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//        return isSuccess[0];
    }

//
//    public enum FileType {//文件类型
//        FILE_MUSIC(0),//
//        FILE_PIC(1),//
//        FILE_VIDEO(2),//
//        FILE_APK(3),
//        FILE_DOC(4),
//        FILE_ZIP(5),
//        FILE_GARBAGE(6),//清理缓存垃圾时用,清理log/temp/apk文件
//        FILE_OTHER(7);//
//
//        public int value;
//
//        FileType(int value) {
//            this.value = value;
//        }
//    }


//    /**
//     * 将字符串写入到文本文件中
//     */
//    public static void writeTxtToFile(Context context, String key,
//                                      String value) {
//        if (TextUtils.isEmpty(key)) {
//            return;
//        }
//        if (TextUtils.isEmpty(value)) {
//            value = "null";
//        }
//
//        String strcontent = ApkUtil.getJson(context, key, value);
//        // 生成文件夹之后，再生成文件，不然会出错
//        makeFilePath(filePath, fileName);// 生成文件
//        String strFilePath = filePath + fileName;
//        // 每次写入时，都换行写
//        String strContent = strcontent + "\r\n";
//        try {
//            File file = new File(strFilePath);
//            if (!file.exists()) {
//                Log.d("TestFile", "Create the file:" + strFilePath);
//                file.getParentFile().mkdirs();
//                file.createNewFile();
//            }
//            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//            raf.seek(file.length());
//            raf.write(strContent.getBytes());
//            raf.close();
//        } catch (Exception e) {
//            Log.e("error:", e + "");
//        }
//    }

//    /**
//     * 生成文件
//     */
//    public static File makeFilePath(String filePath, String fileName) {
//        File file = null;
//        makeRootDirectory(filePath);// 生成文件夹
//        try {
//            file = new File(filePath + fileName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return file;
//    }
//
//    /**
//     * 生成文件夹
//     */
//    public static void makeRootDirectory(String filePath) {
//        File file = null;
//        try {
//            file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdir();
//            }
//        } catch (Exception e) {
//            Log.i("error:", e + "");
//        }
//    }
//
//    /**
//     * 测试上传文件
//     */
//    public static void testUploadFile() {
//        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
//        CloseableHttpClient httpClient = httpClientBuilder.build();
//        HttpPost post = new HttpPost("http://127.0.0.1:9000/acquisition/fridge/up-file");
//
//        //参数
//        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
//        FileBody bin = new FileBody(new File("C:\\Users\\lc\\Desktop\\1.txt"));
//
//        multipartEntityBuilder.addPart("file", bin);
//        post.setEntity(multipartEntityBuilder.build());
//        CloseableHttpResponse response = httpClient.execute(post);
//        try {
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                String result = EntityUtils.toString(entity);
//                System.out.println(result);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            response.close();
//        }

//        String strFilePath = filePath + fileName;
//        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
//        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
//        File file = new File(strFilePath);
//        if (file.exists()) {
//            params.addBodyParameter("file", file);
//            httpUtils.send(HttpRequest.HttpMethod.POST, HttpConnect.UPLOAD_EVENT_FILE, params, new RequestCallBack<String>() {
//
//                @Override
//                public void onSuccess(ResponseInfo<String> responseInfo) {
//                    Log.i("info", "==============responseInfo:" + responseInfo.result);
//                }
//
//                @Override
//                public void onFailure(HttpException e, String s) {
//                    Log.i("info", "==============HttpException:" + e.getMessage());
//                }
//            });
//        }
//    }

//    public static void upLoadByHttpClient4() throws ClientProtocolException,
//            IOException {
////        HttpClient httpclient = new DefaultHttpClient();
////        httpclient.getParams().setParameter(
////                CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
////        HttpPost httppost = new HttpPost(HttpConnect.UPLOAD_EVENT_FILE);
////        String strFilePath = filePath + fileName;
////        File file = new File(strFilePath);
////        MultipartEntity entity = new MultipartEntity();
////        FileBody fileBody = new FileBody(file);
////        entity.addPart("file", fileBody);
////        httppost.setEntity(entity);
////        HttpResponse response = httpclient.execute(httppost);
////        HttpEntity resEntity = response.getEntity();
////        if (resEntity != null) {
////            Log.i("info", EntityUtils.toString(resEntity));
////        }
////        if (resEntity != null) {
////            resEntity.consumeContent();
////        }
////        httpclient.getConnectionManager().shutdown();
//        //新建一个httpclient类
//        HttpClient httpclient = new DefaultHttpClient();
//        //用post方式实现文件上传
//        HttpPost post = new HttpPost(HttpConnect.UPLOAD_EVENT_FILE);
//        //拿到上传的图片
//        String Path = filePath + fileName;
//        //处理上传的文件
//        FileBody fileBody = new FileBody(new File(Path));
//        MultipartEntity entity = new MultipartEntity();
//        entity.addPart("file", fileBody);
//        post.setEntity(entity);
//        HttpResponse response = null;
//        try {
//            response = httpclient.execute(post);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
//
//            //拿到返回的实体，一般而言，用post请求方式返回的流是用这个实体拿到，在这里不需要所以博主就不写了。
//            HttpEntity entitys = response.getEntity();
//            if (entity != null) {
//                System.out.println(entity.getContentLength());
//                try {
//                    System.out.println(EntityUtils.toString(entitys));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        httpclient.getConnectionManager().shutdown();
//
//    }

//    /**
//     * 格式化单位
//     *
//     * @param size
//     * @return
//     */
//    public static String getFormatSize(double size) {
//        double kiloByte = size / 1024;
//        if (kiloByte < 1) {
//            return size + "Byte";
//        }
//
//        double megaByte = kiloByte / 1024;
//        if (megaByte < 1) {
//            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
//            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
//                    .toPlainString() + "KB";
//        }
//
//        double gigaByte = megaByte / 1024;
//        if (gigaByte < 1) {
//            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
//            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
//                    .toPlainString() + "MB";
//        }
//
//        double teraBytes = gigaByte / 1024;
//        if (teraBytes < 1) {
//            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
//            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
//                    .toPlainString() + "GB";
//        }
//        BigDecimal result4 = new BigDecimal(teraBytes);
//        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
//                + "TB";
//    }
//}
