.class public Lcom/example/dummydex/DummyApplication;
.super Landroid/app/Application;
.source "DummyApplication.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "ayuan-log"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 17
    invoke-direct {p0}, Landroid/app/Application;-><init>()V

    return-void
.end method

.method private copyDex(Ljava/lang/String;)Ljava/lang/String;
    .locals 8
    .param p1, "dexName"    # Ljava/lang/String;

    .line 98
    invoke-virtual {p0}, Lcom/example/dummydex/DummyApplication;->getAssets()Landroid/content/res/AssetManager;

    move-result-object v0

    .line 100
    .local v0, "as":Landroid/content/res/AssetManager;
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p0}, Lcom/example/dummydex/DummyApplication;->getFilesDir()Ljava/io/File;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    sget-object v2, Ljava/io/File;->separator:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    .line 102
    .local v1, "path":Ljava/lang/String;
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "copyDex: path: "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    const-string v3, "ayuan-log"

    invoke-static {v3, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 106
    :try_start_0
    new-instance v2, Ljava/io/FileOutputStream;

    invoke-direct {v2, v1}, Ljava/io/FileOutputStream;-><init>(Ljava/lang/String;)V

    .line 108
    .local v2, "out":Ljava/io/FileOutputStream;
    invoke-virtual {v0, p1}, Landroid/content/res/AssetManager;->open(Ljava/lang/String;)Ljava/io/InputStream;

    move-result-object v3

    .line 110
    .local v3, "is":Ljava/io/InputStream;
    const/16 v4, 0x400

    new-array v4, v4, [B

    .line 111
    .local v4, "buffer":[B
    const/4 v5, 0x0

    .line 112
    .local v5, "len":I
    :goto_0
    invoke-virtual {v3, v4}, Ljava/io/InputStream;->read([B)I

    move-result v6

    move v5, v6

    const/4 v7, -0x1

    if-eq v6, v7, :cond_0

    .line 113
    const/4 v6, 0x0

    invoke-virtual {v2, v4, v6, v5}, Ljava/io/FileOutputStream;->write([BII)V

    goto :goto_0

    .line 116
    :cond_0
    invoke-virtual {v2}, Ljava/io/FileOutputStream;->close()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 120
    .end local v2    # "out":Ljava/io/FileOutputStream;
    .end local v3    # "is":Ljava/io/InputStream;
    .end local v4    # "buffer":[B
    .end local v5    # "len":I
    nop

    .line 121
    return-object v1

    .line 117
    :catch_0
    move-exception v2

    .line 118
    .local v2, "e":Ljava/lang/Exception;
    invoke-virtual {v2}, Ljava/lang/Exception;->printStackTrace()V

    .line 119
    const-string v3, ""

    return-object v3
.end method

.method private getLoader(Ljava/lang/String;)Ldalvik/system/DexClassLoader;
    .locals 4
    .param p1, "dexPath"    # Ljava/lang/String;

    .line 85
    new-instance v0, Ldalvik/system/DexClassLoader;

    .line 87
    invoke-virtual {p0}, Lcom/example/dummydex/DummyApplication;->getCacheDir()Ljava/io/File;

    move-result-object v1

    invoke-virtual {v1}, Ljava/io/File;->toString()Ljava/lang/String;

    move-result-object v1

    .line 89
    invoke-virtual {p0}, Lcom/example/dummydex/DummyApplication;->getClassLoader()Ljava/lang/ClassLoader;

    move-result-object v2

    const/4 v3, 0x0

    invoke-direct {v0, p1, v1, v3, v2}, Ldalvik/system/DexClassLoader;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V

    .line 91
    .local v0, "dexClassLoader":Ldalvik/system/DexClassLoader;
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "getLoader: "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    const-string v2, "ayuan-log"

    invoke-static {v2, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 92
    return-object v0
.end method

.method private loadDex()V
    .locals 4

    .line 33
    const-string v0, "ayuan-log"

    const-string v1, "DummyApplication.attachBaseContext: \u5f00\u59cb\u52a0\u8f7d"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 35
    const-string v1, "src.dex"

    invoke-direct {p0, v1}, Lcom/example/dummydex/DummyApplication;->copyDex(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 37
    .local v1, "dexPath":Ljava/lang/String;
    invoke-direct {p0, v1}, Lcom/example/dummydex/DummyApplication;->getLoader(Ljava/lang/String;)Ldalvik/system/DexClassLoader;

    move-result-object v2

    .line 39
    .local v2, "dexClassLoader":Ldalvik/system/DexClassLoader;
    invoke-direct {p0, v2}, Lcom/example/dummydex/DummyApplication;->replaceClassLoader(Ldalvik/system/DexClassLoader;)V

    .line 40
    const-string v3, "DummyApplication.attachBaseContext: \u52a0\u8f7d\u7ed3\u675f"

    invoke-static {v0, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 41
    return-void
.end method

.method private replaceClassLoader(Ldalvik/system/DexClassLoader;)V
    .locals 12
    .param p1, "dexClassLoader"    # Ldalvik/system/DexClassLoader;

    .line 48
    :try_start_0
    const-string v0, "android.app.ActivityThread"

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    .line 50
    .local v0, "clzActivityThread":Ljava/lang/Class;
    const-string v1, "currentActivityThread"

    const/4 v2, 0x0

    new-array v3, v2, [Ljava/lang/Class;

    invoke-virtual {v0, v1, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 52
    .local v1, "methodCurrentActivityThread":Ljava/lang/reflect/Method;
    const/4 v3, 0x0

    new-array v2, v2, [Ljava/lang/Object;

    invoke-virtual {v1, v3, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    .line 55
    .local v2, "sCurrentActivityThread":Ljava/lang/Object;
    const-string v3, "android.app.ActivityThread$AppBindData"

    invoke-static {v3}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v3

    .line 57
    .local v3, "clzAppBinData":Ljava/lang/Class;
    const-string v4, "mBoundApplication"

    invoke-virtual {v0, v4}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v4

    .line 59
    .local v4, "fieldBoundApplication":Ljava/lang/reflect/Field;
    const/4 v5, 0x1

    invoke-virtual {v4, v5}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    .line 60
    invoke-virtual {v4, v2}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v6

    .line 63
    .local v6, "mBoundApplication":Ljava/lang/Object;
    const-string v7, "android.app.LoadedApk"

    invoke-static {v7}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v7

    .line 65
    .local v7, "clzLoadedApk":Ljava/lang/Class;
    const-string v8, "info"

    invoke-virtual {v3, v8}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v8

    .line 67
    .local v8, "fieldInfo":Ljava/lang/reflect/Field;
    invoke-virtual {v8, v5}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    .line 68
    invoke-virtual {v8, v6}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v9

    .line 71
    .local v9, "info":Ljava/lang/Object;
    const-string v10, "java.lang.ClassLoader"

    invoke-static {v10}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v10

    .line 73
    .local v10, "clzClassLoader":Ljava/lang/Class;
    const-string v11, "mClassLoader"

    invoke-virtual {v7, v11}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v11

    .line 75
    .local v11, "fieldClassLoader":Ljava/lang/reflect/Field;
    invoke-virtual {v11, v5}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    .line 76
    invoke-virtual {v11, v9, p1}, Ljava/lang/reflect/Field;->set(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 79
    .end local v0    # "clzActivityThread":Ljava/lang/Class;
    .end local v1    # "methodCurrentActivityThread":Ljava/lang/reflect/Method;
    .end local v2    # "sCurrentActivityThread":Ljava/lang/Object;
    .end local v3    # "clzAppBinData":Ljava/lang/Class;
    .end local v4    # "fieldBoundApplication":Ljava/lang/reflect/Field;
    .end local v6    # "mBoundApplication":Ljava/lang/Object;
    .end local v7    # "clzLoadedApk":Ljava/lang/Class;
    .end local v8    # "fieldInfo":Ljava/lang/reflect/Field;
    .end local v9    # "info":Ljava/lang/Object;
    .end local v10    # "clzClassLoader":Ljava/lang/Class;
    .end local v11    # "fieldClassLoader":Ljava/lang/reflect/Field;
    goto :goto_0

    .line 77
    :catch_0
    move-exception v0

    .line 78
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    .line 81
    .end local v0    # "e":Ljava/lang/Exception;
    :goto_0
    return-void
.end method


# virtual methods
.method protected attachBaseContext(Landroid/content/Context;)V
    .locals 2
    .param p1, "base"    # Landroid/content/Context;

    .line 22
    invoke-super {p0, p1}, Landroid/app/Application;->attachBaseContext(Landroid/content/Context;)V

    .line 23
    const-string v0, "packed by ayuan"

    const/4 v1, 0x1

    invoke-static {p1, v0, v1}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v0

    invoke-virtual {v0}, Landroid/widget/Toast;->show()V

    .line 24
    invoke-direct {p0}, Lcom/example/dummydex/DummyApplication;->loadDex()V

    .line 25
    return-void
.end method

.method public onCreate()V
    .locals 0

    .line 29
    invoke-super {p0}, Landroid/app/Application;->onCreate()V

    .line 31
    return-void
.end method
