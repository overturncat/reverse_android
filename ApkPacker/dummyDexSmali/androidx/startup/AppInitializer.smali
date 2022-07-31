.class public final Landroidx/startup/AppInitializer;
.super Ljava/lang/Object;
.source "AppInitializer.java"


# static fields
.field private static final SECTION_NAME:Ljava/lang/String; = "Startup"

.field private static volatile sInstance:Landroidx/startup/AppInitializer;

.field private static final sLock:Ljava/lang/Object;


# instance fields
.field final mContext:Landroid/content/Context;

.field final mDiscovered:Ljava/util/Set;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Set<",
            "Ljava/lang/Class<",
            "+",
            "Landroidx/startup/Initializer<",
            "*>;>;>;"
        }
    .end annotation
.end field

.field final mInitialized:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/Class<",
            "*>;",
            "Ljava/lang/Object;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 55
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    sput-object v0, Landroidx/startup/AppInitializer;->sLock:Ljava/lang/Object;

    return-void
.end method

.method constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    .line 71
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 72
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object v0

    iput-object v0, p0, Landroidx/startup/AppInitializer;->mContext:Landroid/content/Context;

    .line 73
    new-instance v0, Ljava/util/HashSet;

    invoke-direct {v0}, Ljava/util/HashSet;-><init>()V

    iput-object v0, p0, Landroidx/startup/AppInitializer;->mDiscovered:Ljava/util/Set;

    .line 74
    new-instance v0, Ljava/util/HashMap;

    invoke-direct {v0}, Ljava/util/HashMap;-><init>()V

    iput-object v0, p0, Landroidx/startup/AppInitializer;->mInitialized:Ljava/util/Map;

    .line 75
    return-void
.end method

.method public static getInstance(Landroid/content/Context;)Landroidx/startup/AppInitializer;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;

    .line 84
    sget-object v0, Landroidx/startup/AppInitializer;->sInstance:Landroidx/startup/AppInitializer;

    if-nez v0, :cond_1

    .line 85
    sget-object v0, Landroidx/startup/AppInitializer;->sLock:Ljava/lang/Object;

    monitor-enter v0

    .line 86
    :try_start_0
    sget-object v1, Landroidx/startup/AppInitializer;->sInstance:Landroidx/startup/AppInitializer;

    if-nez v1, :cond_0

    .line 87
    new-instance v1, Landroidx/startup/AppInitializer;

    invoke-direct {v1, p0}, Landroidx/startup/AppInitializer;-><init>(Landroid/content/Context;)V

    sput-object v1, Landroidx/startup/AppInitializer;->sInstance:Landroidx/startup/AppInitializer;

    .line 89
    :cond_0
    monitor-exit v0

    goto :goto_0

    :catchall_0
    move-exception v1

    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1

    .line 91
    :cond_1
    :goto_0
    sget-object v0, Landroidx/startup/AppInitializer;->sInstance:Landroidx/startup/AppInitializer;

    return-object v0
.end method


# virtual methods
.method discoverAndInitialize()V
    .locals 12

    .line 177
    :try_start_0
    const-string v0, "Startup"

    invoke-static {v0}, Landroidx/tracing/Trace;->beginSection(Ljava/lang/String;)V

    .line 178
    new-instance v0, Landroid/content/ComponentName;

    iget-object v1, p0, Landroidx/startup/AppInitializer;->mContext:Landroid/content/Context;

    invoke-virtual {v1}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v1

    const-class v2, Landroidx/startup/InitializationProvider;

    .line 179
    invoke-virtual {v2}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v0, v1, v2}, Landroid/content/ComponentName;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    .line 180
    .local v0, "provider":Landroid/content/ComponentName;
    iget-object v1, p0, Landroidx/startup/AppInitializer;->mContext:Landroid/content/Context;

    invoke-virtual {v1}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v1

    const/16 v2, 0x80

    .line 181
    invoke-virtual {v1, v0, v2}, Landroid/content/pm/PackageManager;->getProviderInfo(Landroid/content/ComponentName;I)Landroid/content/pm/ProviderInfo;

    move-result-object v1

    .line 182
    .local v1, "providerInfo":Landroid/content/pm/ProviderInfo;
    iget-object v2, v1, Landroid/content/pm/ProviderInfo;->metaData:Landroid/os/Bundle;

    .line 183
    .local v2, "metadata":Landroid/os/Bundle;
    iget-object v3, p0, Landroidx/startup/AppInitializer;->mContext:Landroid/content/Context;

    sget v4, Landroidx/startup/R$string;->androidx_startup:I

    invoke-virtual {v3, v4}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v3

    .line 184
    .local v3, "startup":Ljava/lang/String;
    if-eqz v2, :cond_1

    .line 185
    new-instance v4, Ljava/util/HashSet;

    invoke-direct {v4}, Ljava/util/HashSet;-><init>()V

    .line 186
    .local v4, "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    invoke-virtual {v2}, Landroid/os/Bundle;->keySet()Ljava/util/Set;

    move-result-object v5

    .line 187
    .local v5, "keys":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    invoke-interface {v5}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v6

    :goto_0
    invoke-interface {v6}, Ljava/util/Iterator;->hasNext()Z

    move-result v7

    if-eqz v7, :cond_1

    invoke-interface {v6}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Ljava/lang/String;

    .line 188
    .local v7, "key":Ljava/lang/String;
    const/4 v8, 0x0

    invoke-virtual {v2, v7, v8}, Landroid/os/Bundle;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    .line 189
    .local v8, "value":Ljava/lang/String;
    invoke-virtual {v3, v8}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v9

    if-eqz v9, :cond_0

    .line 190
    invoke-static {v7}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v9

    .line 191
    .local v9, "clazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-class v10, Landroidx/startup/Initializer;

    invoke-virtual {v10, v9}, Ljava/lang/Class;->isAssignableFrom(Ljava/lang/Class;)Z

    move-result v10

    if-eqz v10, :cond_0

    .line 192
    move-object v10, v9

    .line 194
    .local v10, "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    iget-object v11, p0, Landroidx/startup/AppInitializer;->mDiscovered:Ljava/util/Set;

    invoke-interface {v11, v10}, Ljava/util/Set;->add(Ljava/lang/Object;)Z

    .line 198
    invoke-virtual {p0, v10, v4}, Landroidx/startup/AppInitializer;->doInitialize(Ljava/lang/Class;Ljava/util/Set;)Ljava/lang/Object;
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/ClassNotFoundException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 201
    .end local v7    # "key":Ljava/lang/String;
    .end local v8    # "value":Ljava/lang/String;
    .end local v9    # "clazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v10    # "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    :cond_0
    goto :goto_0

    .line 206
    .end local v0    # "provider":Landroid/content/ComponentName;
    .end local v1    # "providerInfo":Landroid/content/pm/ProviderInfo;
    .end local v2    # "metadata":Landroid/os/Bundle;
    .end local v3    # "startup":Ljava/lang/String;
    .end local v4    # "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    .end local v5    # "keys":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    :cond_1
    invoke-static {}, Landroidx/tracing/Trace;->endSection()V

    .line 207
    nop

    .line 208
    return-void

    .line 206
    :catchall_0
    move-exception v0

    goto :goto_2

    .line 203
    :catch_0
    move-exception v0

    goto :goto_1

    :catch_1
    move-exception v0

    .line 204
    .local v0, "exception":Ljava/lang/Exception;
    :goto_1
    :try_start_1
    new-instance v1, Landroidx/startup/StartupException;

    invoke-direct {v1, v0}, Landroidx/startup/StartupException;-><init>(Ljava/lang/Throwable;)V

    throw v1
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 206
    .end local v0    # "exception":Ljava/lang/Exception;
    :goto_2
    invoke-static {}, Landroidx/tracing/Trace;->endSection()V

    .line 207
    throw v0
.end method

.method doInitialize(Ljava/lang/Class;Ljava/util/Set;)Ljava/lang/Object;
    .locals 8
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">(",
            "Ljava/lang/Class<",
            "+",
            "Landroidx/startup/Initializer<",
            "*>;>;",
            "Ljava/util/Set<",
            "Ljava/lang/Class<",
            "*>;>;)TT;"
        }
    .end annotation

    .line 123
    .local p1, "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    .local p2, "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    sget-object v0, Landroidx/startup/AppInitializer;->sLock:Ljava/lang/Object;

    monitor-enter v0

    .line 124
    :try_start_0
    invoke-static {}, Landroidx/tracing/Trace;->isEnabled()Z

    move-result v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_2

    .line 126
    .local v1, "isTracingEnabled":Z
    if-eqz v1, :cond_0

    .line 128
    :try_start_1
    invoke-virtual {p1}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Landroidx/tracing/Trace;->beginSection(Ljava/lang/String;)V

    .line 130
    :cond_0
    invoke-interface {p2, p1}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v2

    const/4 v3, 0x0

    if-nez v2, :cond_4

    .line 137
    iget-object v2, p0, Landroidx/startup/AppInitializer;->mInitialized:Ljava/util/Map;

    invoke-interface {v2, p1}, Ljava/util/Map;->containsKey(Ljava/lang/Object;)Z

    move-result v2

    if-nez v2, :cond_3

    .line 138
    invoke-interface {p2, p1}, Ljava/util/Set;->add(Ljava/lang/Object;)Z
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    .line 140
    :try_start_2
    new-array v2, v3, [Ljava/lang/Class;

    invoke-virtual {p1, v2}, Ljava/lang/Class;->getDeclaredConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;

    move-result-object v2

    new-array v3, v3, [Ljava/lang/Object;

    invoke-virtual {v2, v3}, Ljava/lang/reflect/Constructor;->newInstance([Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    .line 141
    .local v2, "instance":Ljava/lang/Object;
    move-object v3, v2

    check-cast v3, Landroidx/startup/Initializer;

    .line 142
    .local v3, "initializer":Landroidx/startup/Initializer;, "Landroidx/startup/Initializer<*>;"
    nop

    .line 143
    invoke-interface {v3}, Landroidx/startup/Initializer;->dependencies()Ljava/util/List;

    move-result-object v4

    .line 145
    .local v4, "dependencies":Ljava/util/List;, "Ljava/util/List<Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;>;"
    invoke-interface {v4}, Ljava/util/List;->isEmpty()Z

    move-result v5

    if-nez v5, :cond_2

    .line 146
    invoke-interface {v4}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_0
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_2

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/lang/Class;

    .line 147
    .local v6, "clazz":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    iget-object v7, p0, Landroidx/startup/AppInitializer;->mInitialized:Ljava/util/Map;

    invoke-interface {v7, v6}, Ljava/util/Map;->containsKey(Ljava/lang/Object;)Z

    move-result v7

    if-nez v7, :cond_1

    .line 148
    invoke-virtual {p0, v6, p2}, Landroidx/startup/AppInitializer;->doInitialize(Ljava/lang/Class;Ljava/util/Set;)Ljava/lang/Object;

    .line 150
    .end local v6    # "clazz":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    :cond_1
    goto :goto_0

    .line 155
    :cond_2
    iget-object v5, p0, Landroidx/startup/AppInitializer;->mContext:Landroid/content/Context;

    invoke-interface {v3, v5}, Landroidx/startup/Initializer;->create(Landroid/content/Context;)Ljava/lang/Object;

    move-result-object v5

    .line 159
    .local v5, "result":Ljava/lang/Object;
    invoke-interface {p2, p1}, Ljava/util/Set;->remove(Ljava/lang/Object;)Z

    .line 160
    iget-object v6, p0, Landroidx/startup/AppInitializer;->mInitialized:Ljava/util/Map;

    invoke-interface {v6, p1, v5}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    .line 163
    .end local v2    # "instance":Ljava/lang/Object;
    .end local v3    # "initializer":Landroidx/startup/Initializer;, "Landroidx/startup/Initializer<*>;"
    .end local v4    # "dependencies":Ljava/util/List;, "Ljava/util/List<Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;>;"
    goto :goto_1

    .line 161
    .end local v5    # "result":Ljava/lang/Object;
    :catchall_0
    move-exception v2

    .line 162
    .local v2, "throwable":Ljava/lang/Throwable;
    :try_start_3
    new-instance v3, Landroidx/startup/StartupException;

    invoke-direct {v3, v2}, Landroidx/startup/StartupException;-><init>(Ljava/lang/Throwable;)V

    .end local v1    # "isTracingEnabled":Z
    .end local p1    # "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    .end local p2    # "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    throw v3

    .line 165
    .end local v2    # "throwable":Ljava/lang/Throwable;
    .restart local v1    # "isTracingEnabled":Z
    .restart local p1    # "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    .restart local p2    # "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    :cond_3
    iget-object v2, p0, Landroidx/startup/AppInitializer;->mInitialized:Ljava/util/Map;

    invoke-interface {v2, p1}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    move-object v5, v2

    .line 167
    .restart local v5    # "result":Ljava/lang/Object;
    :goto_1
    nop

    .line 169
    :try_start_4
    invoke-static {}, Landroidx/tracing/Trace;->endSection()V

    monitor-exit v0
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_2

    .line 167
    return-object v5

    .line 131
    .end local v5    # "result":Ljava/lang/Object;
    :cond_4
    :try_start_5
    const-string v2, "Cannot initialize %s. Cycle detected."

    const/4 v4, 0x1

    new-array v4, v4, [Ljava/lang/Object;

    .line 132
    invoke-virtual {p1}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v5

    aput-object v5, v4, v3

    .line 131
    invoke-static {v2, v4}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    .line 134
    .local v2, "message":Ljava/lang/String;
    new-instance v3, Ljava/lang/IllegalStateException;

    invoke-direct {v3, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    .end local v1    # "isTracingEnabled":Z
    .end local p1    # "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    .end local p2    # "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    throw v3
    :try_end_5
    .catchall {:try_start_5 .. :try_end_5} :catchall_1

    .line 169
    .end local v2    # "message":Ljava/lang/String;
    .restart local v1    # "isTracingEnabled":Z
    .restart local p1    # "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    .restart local p2    # "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    :catchall_1
    move-exception v2

    :try_start_6
    invoke-static {}, Landroidx/tracing/Trace;->endSection()V

    .line 170
    nop

    .end local p1    # "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    .end local p2    # "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    throw v2

    .line 171
    .end local v1    # "isTracingEnabled":Z
    .restart local p1    # "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    .restart local p2    # "initializing":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/Class<*>;>;"
    :catchall_2
    move-exception v1

    monitor-exit v0
    :try_end_6
    .catchall {:try_start_6 .. :try_end_6} :catchall_2

    throw v1
.end method

.method public initializeComponent(Ljava/lang/Class;)Ljava/lang/Object;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">(",
            "Ljava/lang/Class<",
            "+",
            "Landroidx/startup/Initializer<",
            "TT;>;>;)TT;"
        }
    .end annotation

    .line 104
    .local p1, "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<TT;>;>;"
    new-instance v0, Ljava/util/HashSet;

    invoke-direct {v0}, Ljava/util/HashSet;-><init>()V

    invoke-virtual {p0, p1, v0}, Landroidx/startup/AppInitializer;->doInitialize(Ljava/lang/Class;Ljava/util/Set;)Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method public isEagerlyInitialized(Ljava/lang/Class;)Z
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Class<",
            "+",
            "Landroidx/startup/Initializer<",
            "*>;>;)Z"
        }
    .end annotation

    .line 115
    .local p1, "component":Ljava/lang/Class;, "Ljava/lang/Class<+Landroidx/startup/Initializer<*>;>;"
    iget-object v0, p0, Landroidx/startup/AppInitializer;->mDiscovered:Ljava/util/Set;

    invoke-interface {v0, p1}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method
