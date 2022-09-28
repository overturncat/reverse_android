struct{
    uint32_t nbucket;
    uint32_t symoffset;
    uint32_t bloom_size;
    uint32_t bloom_shift;
    uint64_t bloom[bloom_size];
    /*uint32_t 32位二进制文件*/uint32_t bucket[nbucket];
    uint32_t chain[];
};
