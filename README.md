# hashbench-intellij
IntelliJ plugin for benchmarking SHA-256 implementations.

> [!NOTE]
> This repository contains supplementary material of the following paper: 
> ...

## Setup

1. Install Docker
2. Run `docker compose up`. 

## Plugin Usage
To install the plugin, navigate to `File` -> `Settings` -> `Plugins` -> `⚙` -> `Manage Plugin Repositories`. Add the url of the plugin repository ([`https://localhost:8443/`](https://localhost:8443)), click `OK`, and search for the plugin (`hashbench`). After installing, the `Tools` menu should have an item `Run Benchmark`:

![run.png](run.png)

After benchmarking has finished (~5 minutes), the results are shown along with instructions on how to send these by email to a configured email address. You can change the mail address in [ResultDialog.java](src/main/java/hashbench/benchmark/ResultDialog.java). If you do, remember to rebuild the docker image.

## Benchmarking Methodology
Benchmarking is performed by iterative hashing on a constant input. The plugin measures the single-thread performance in hashes per second (H/s) of the following SHA-256 implementations:

- The implementation provided by the JRE used to run IntelliJ.
- The third-party implementation of the [BouncyCastle](https://www.bouncycastle.org/) library.

While there are other libraries  that offer SHA-256 implementations (such as [Google Guava](https://github.com/google/guava) and [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/)), we decided to exclude them as these are wrappers around the implementation provided by the JRE.

Benchmarking is subject to various non-deterministic factors that may bias the results. Thus, we:

- run some iterations for each implementation as warm-up before measuring performance. (To address bias caused by system load, priming of caches, and JIT compilation)
- keep references to the computed hashes although we are not interested in them. (To address bias caused by performance optimizations, such as statement reordering)
- perform multiple runs in random order, where each run is performed in a new thread, with only one thread being running at any given time. After all runs are performed, we calculate the final hash rate by averaging the hash rates measured in each run. (To address bias caused by mapping Java threads to native threads on the operating system)
- prompt to shutdown other programs before benchmarking. Moreover, we pause for 30 seconds between runs. (To address bias caused by system load)
- use the `System.nanoTime()` method for measuring time. (To address bias caused by NTP clock adjustments)

Before benchmarking, the plugin extracts various properties of the runtime environment. These include details about the operating system, hardware, and the JRE. To extract these properties, we use the [OSHI](https://github.com/oshi/oshi) library and the `System.getProperty()` method.

## Results Format

<details>

```json
{
  "runsPerImpl": 5,
  "warmupRunsPerImpl": 1,
  "iterations": 2500000,
  "pauseBetweenRuns": 30000,
  "input": "mmustermd8L_sA$9",
  "results": [
    {
      "implementation": "BouncyCastleSHA256",
      "hash": "3cd28e33a05b816fd9625048ed7d0466e640fae75b2c54058deff0ec2a7b962a",
      "elapsedMillisecondsMean": 749.1354890199999,
      "elapsedMillisecondsStd": 13.368216600274456,
      "hashesPerSecondMean": 3338213.7143915812,
      "hashesPerSecondStd": 57950.2041334752
    },
    {
      "implementation": "JRESHA256",
      "hash": "3cd28e33a05b816fd9625048ed7d0466e640fae75b2c54058deff0ec2a7b962a",
      "elapsedMillisecondsMean": 162.13316344,
      "elapsedMillisecondsStd": 3.203018146042761,
      "hashesPerSecondMean": 1.542536318794936e7,
      "hashesPerSecondStd": 300719.91718990944
    }
  ],
  "sysInfo": {
    "osFamily": "Linux Mint",
    "osManufacturer": "GNU/Linux",
    "osVersion": {
      "version": "22",
      "codeName": "Wilma",
      "buildNumber": "6.8.0-45-generic"
    },
    "computerSystem": {
      "firmware": {
        "manufacturer": "unknown",
        "description": "dmi:bvnLENOVO:bvrR1MET57W(1.27):bd06/03/2024:br1.27:efr1.27:svnLENOVO:pn21A0005EGE:pvrThinkPadP14sGen2a:rvnLENOVO:rn21A0005EGE:rvrSDK0J40697WIN:cvnLENOVO:ct10:cvrNone:skuLENOVO_MT_21A0_BU_Think_FM_ThinkPadP14sGen2a:",
        "version": "R1MET57W (1.27 )",
        "releaseDate": "2024-06-03",
        "name": "unknown"
      },
      "baseboard": {
        "manufacturer": "LENOVO",
        "model": "21A0005EGE",
        "version": "SDK0J40697 WIN"
      },
      "manufacturer": "LENOVO",
      "model": "21A0005EGE (version: ThinkPad P14s Gen 2a)"
    },
    "cpu": {
      "maxFreq": 4507000000,
      "currentFreq": [
        3253165000, 3109522000, 3251281000, 400000000, 3144334000, 3289603000,
        3872273000, 3632463000, 3967473000, 3967444000, 3177580000, 400000000,
        3972012000, 3285445000, 3232462000, 3034059000
      ],
      "contextSwitches": 2101660821,
      "interrupts": 1139219723,
      "systemCpuLoadTicks": [
        60415650, 57600, 18175570, 2859478430, 1894720, 0, 801980, 0
      ],
      "processorCpuLoadTicks": [
        [4179680, 5000, 1310660, 177609580, 164230, 0, 506510, 0],
        [3442770, 9310, 906830, 179536360, 79350, 0, 9090, 0],
        [4288610, 860, 1376240, 177678980, 160730, 0, 34610, 0],
        [3402250, 1090, 828430, 179791870, 74450, 0, 2710, 0],
        [4336420, 4480, 1328770, 177894430, 162220, 0, 5230, 0],
        [3082420, 2050, 939410, 179272530, 81310, 0, 2290, 0],
        [4189120, 4100, 1657290, 177664410, 161100, 0, 10770, 0],
        [3331790, 2290, 1081130, 179421940, 67110, 0, 10070, 0],
        [4078610, 2230, 1348200, 177953420, 157800, 0, 33110, 0],
        [3358170, 1720, 817210, 179743310, 64110, 0, 73380, 0],
        [4212490, 4770, 1394410, 177802280, 163890, 0, 31520, 0],
        [3310150, 7930, 844670, 179725840, 73620, 0, 32550, 0],
        [4215370, 1680, 1394550, 177796540, 163610, 0, 25150, 0],
        [3318340, 3880, 831950, 179873980, 83200, 0, 2970, 0],
        [4251090, 3930, 1345080, 177861370, 167190, 0, 19070, 0],
        [3418320, 2200, 770680, 179851520, 70720, 0, 2870, 0]
      ],
      "physicalPackageCount": 1,
      "physicalProcessorCount": 8,
      "logicalProcessorCount": 16,
      "logicalProcessors": [
        {
          "processorNumber": 0,
          "physicalProcessorNumber": 0,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 1,
          "physicalProcessorNumber": 0,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 2,
          "physicalProcessorNumber": 1,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 3,
          "physicalProcessorNumber": 1,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 4,
          "physicalProcessorNumber": 2,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 5,
          "physicalProcessorNumber": 2,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 6,
          "physicalProcessorNumber": 3,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 7,
          "physicalProcessorNumber": 3,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 8,
          "physicalProcessorNumber": 4,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 9,
          "physicalProcessorNumber": 4,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 10,
          "physicalProcessorNumber": 5,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 11,
          "physicalProcessorNumber": 5,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 12,
          "physicalProcessorNumber": 6,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 13,
          "physicalProcessorNumber": 6,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 14,
          "physicalProcessorNumber": 7,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        },
        {
          "processorNumber": 15,
          "physicalProcessorNumber": 7,
          "physicalPackageNumber": 0,
          "numaNode": 0,
          "processorGroup": 0
        }
      ],
      "physicalProcessors": [
        {
          "physicalPackageNumber": 0,
          "physicalProcessorNumber": 0,
          "efficiency": 0,
          "idString": "cpu:type:x86,ven0002fam0019mod0050:feature:,0000,0001,0002,0003,0004,0005,0006,0007,0008,0009,000B,000C,000D,000E,000F,0010,0011,0013,0017,0018,0019,001A,001C,0020,0021,0022,0023,0024,0025,0026,0027,0028,0029,002B,002C,002D,002E,002F,0030,0031,0034,0036,0037,0038,0039,003A,003B,003D,0064,0068,006E,0070,0074,0075,0078,0079,007A,007C,007D,0080,0081,0083,0089,008C,008D,0093,0094,0096,0097,0099,009A,009B,009C,009D,009E,00C0,00C1,00C2,00C3,00C4,00C5,00C6,00C7,00C8,00C9,00CA,00CC,00CD,00D1,00D6,00D7,00D8,00DA,00DC,00DD,00DE,00E2,00E4,00E6,00E8,00EA,00ED,00F0,00F1,00F2,00F3,00F5,00F6,00F9,00FA,00FB,00FC,010F,0120,0123,0125,0127,0128,0129,012A,012C,012F,0132,0133,0134,0137,0138,013D,0140,0141,0142,0143,0160,0161,0162,0163,0165,016C,016E,016F,0177,0179,017D,018B,01A0,01A1,01A2,01A4,01A6,01A9,01AA,01AC,01AD,01AE,01AF,01B1,01B2,01B3,01B4,01B8,01BB,01BC,01BD,01C2,01E0,01E1,01E2,01E3,01E4,01E5,01E6,01E7,01EA,01EB,01EC,01ED,01EF,01F0,01F1,01F3,01F4,01FC,0202,0203,0204,0207,0209,020A,0216,0220,0221,0223,0224,0225,0244,0262,026B,026C,026D,026E,0270,0280,0282,0283,0286,029B,029C"
        },
        {
          "physicalPackageNumber": 0,
          "physicalProcessorNumber": 1,
          "efficiency": 0,
          "idString": "cpu:type:x86,ven0002fam0019mod0050:feature:,0000,0001,0002,0003,0004,0005,0006,0007,0008,0009,000B,000C,000D,000E,000F,0010,0011,0013,0017,0018,0019,001A,001C,0020,0021,0022,0023,0024,0025,0026,0027,0028,0029,002B,002C,002D,002E,002F,0030,0031,0034,0036,0037,0038,0039,003A,003B,003D,0064,0068,006E,0070,0074,0075,0078,0079,007A,007C,007D,0080,0081,0083,0089,008C,008D,0093,0094,0096,0097,0099,009A,009B,009C,009D,009E,00C0,00C1,00C2,00C3,00C4,00C5,00C6,00C7,00C8,00C9,00CA,00CC,00CD,00D1,00D6,00D7,00D8,00DA,00DC,00DD,00DE,00E2,00E4,00E6,00E8,00EA,00ED,00F0,00F1,00F2,00F3,00F5,00F6,00F9,00FA,00FB,00FC,010F,0120,0123,0125,0127,0128,0129,012A,012C,012F,0132,0133,0134,0137,0138,013D,0140,0141,0142,0143,0160,0161,0162,0163,0165,016C,016E,016F,0177,0179,017D,018B,01A0,01A1,01A2,01A4,01A6,01A9,01AA,01AC,01AD,01AE,01AF,01B1,01B2,01B3,01B4,01B8,01BB,01BC,01BD,01C2,01E0,01E1,01E2,01E3,01E4,01E5,01E6,01E7,01EA,01EB,01EC,01ED,01EF,01F0,01F1,01F3,01F4,01FC,0202,0203,0204,0207,0209,020A,0216,0220,0221,0223,0224,0225,0244,0262,026B,026C,026D,026E,0270,0280,0282,0283,0286,029B,029C"
        },
        {
          "physicalPackageNumber": 0,
          "physicalProcessorNumber": 2,
          "efficiency": 0,
          "idString": "cpu:type:x86,ven0002fam0019mod0050:feature:,0000,0001,0002,0003,0004,0005,0006,0007,0008,0009,000B,000C,000D,000E,000F,0010,0011,0013,0017,0018,0019,001A,001C,0020,0021,0022,0023,0024,0025,0026,0027,0028,0029,002B,002C,002D,002E,002F,0030,0031,0034,0036,0037,0038,0039,003A,003B,003D,0064,0068,006E,0070,0074,0075,0078,0079,007A,007C,007D,0080,0081,0083,0089,008C,008D,0093,0094,0096,0097,0099,009A,009B,009C,009D,009E,00C0,00C1,00C2,00C3,00C4,00C5,00C6,00C7,00C8,00C9,00CA,00CC,00CD,00D1,00D6,00D7,00D8,00DA,00DC,00DD,00DE,00E2,00E4,00E6,00E8,00EA,00ED,00F0,00F1,00F2,00F3,00F5,00F6,00F9,00FA,00FB,00FC,010F,0120,0123,0125,0127,0128,0129,012A,012C,012F,0132,0133,0134,0137,0138,013D,0140,0141,0142,0143,0160,0161,0162,0163,0165,016C,016E,016F,0177,0179,017D,018B,01A0,01A1,01A2,01A4,01A6,01A9,01AA,01AC,01AD,01AE,01AF,01B1,01B2,01B3,01B4,01B8,01BB,01BC,01BD,01C2,01E0,01E1,01E2,01E3,01E4,01E5,01E6,01E7,01EA,01EB,01EC,01ED,01EF,01F0,01F1,01F3,01F4,01FC,0202,0203,0204,0207,0209,020A,0216,0220,0221,0223,0224,0225,0244,0262,026B,026C,026D,026E,0270,0280,0282,0283,0286,029B,029C"
        },
        {
          "physicalPackageNumber": 0,
          "physicalProcessorNumber": 3,
          "efficiency": 0,
          "idString": "cpu:type:x86,ven0002fam0019mod0050:feature:,0000,0001,0002,0003,0004,0005,0006,0007,0008,0009,000B,000C,000D,000E,000F,0010,0011,0013,0017,0018,0019,001A,001C,0020,0021,0022,0023,0024,0025,0026,0027,0028,0029,002B,002C,002D,002E,002F,0030,0031,0034,0036,0037,0038,0039,003A,003B,003D,0064,0068,006E,0070,0074,0075,0078,0079,007A,007C,007D,0080,0081,0083,0089,008C,008D,0093,0094,0096,0097,0099,009A,009B,009C,009D,009E,00C0,00C1,00C2,00C3,00C4,00C5,00C6,00C7,00C8,00C9,00CA,00CC,00CD,00D1,00D6,00D7,00D8,00DA,00DC,00DD,00DE,00E2,00E4,00E6,00E8,00EA,00ED,00F0,00F1,00F2,00F3,00F5,00F6,00F9,00FA,00FB,00FC,010F,0120,0123,0125,0127,0128,0129,012A,012C,012F,0132,0133,0134,0137,0138,013D,0140,0141,0142,0143,0160,0161,0162,0163,0165,016C,016E,016F,0177,0179,017D,018B,01A0,01A1,01A2,01A4,01A6,01A9,01AA,01AC,01AD,01AE,01AF,01B1,01B2,01B3,01B4,01B8,01BB,01BC,01BD,01C2,01E0,01E1,01E2,01E3,01E4,01E5,01E6,01E7,01EA,01EB,01EC,01ED,01EF,01F0,01F1,01F3,01F4,01FC,0202,0203,0204,0207,0209,020A,0216,0220,0221,0223,0224,0225,0244,0262,026B,026C,026D,026E,0270,0280,0282,0283,0286,029B,029C"
        },
        {
          "physicalPackageNumber": 0,
          "physicalProcessorNumber": 4,
          "efficiency": 0,
          "idString": "cpu:type:x86,ven0002fam0019mod0050:feature:,0000,0001,0002,0003,0004,0005,0006,0007,0008,0009,000B,000C,000D,000E,000F,0010,0011,0013,0017,0018,0019,001A,001C,0020,0021,0022,0023,0024,0025,0026,0027,0028,0029,002B,002C,002D,002E,002F,0030,0031,0034,0036,0037,0038,0039,003A,003B,003D,0064,0068,006E,0070,0074,0075,0078,0079,007A,007C,007D,0080,0081,0083,0089,008C,008D,0093,0094,0096,0097,0099,009A,009B,009C,009D,009E,00C0,00C1,00C2,00C3,00C4,00C5,00C6,00C7,00C8,00C9,00CA,00CC,00CD,00D1,00D6,00D7,00D8,00DA,00DC,00DD,00DE,00E2,00E4,00E6,00E8,00EA,00ED,00F0,00F1,00F2,00F3,00F5,00F6,00F9,00FA,00FB,00FC,010F,0120,0123,0125,0127,0128,0129,012A,012C,012F,0132,0133,0134,0137,0138,013D,0140,0141,0142,0143,0160,0161,0162,0163,0165,016C,016E,016F,0177,0179,017D,018B,01A0,01A1,01A2,01A4,01A6,01A9,01AA,01AC,01AD,01AE,01AF,01B1,01B2,01B3,01B4,01B8,01BB,01BC,01BD,01C2,01E0,01E1,01E2,01E3,01E4,01E5,01E6,01E7,01EA,01EB,01EC,01ED,01EF,01F0,01F1,01F3,01F4,01FC,0202,0203,0204,0207,0209,020A,0216,0220,0221,0223,0224,0225,0244,0262,026B,026C,026D,026E,0270,0280,0282,0283,0286,029B,029C"
        },
        {
          "physicalPackageNumber": 0,
          "physicalProcessorNumber": 5,
          "efficiency": 0,
          "idString": "cpu:type:x86,ven0002fam0019mod0050:feature:,0000,0001,0002,0003,0004,0005,0006,0007,0008,0009,000B,000C,000D,000E,000F,0010,0011,0013,0017,0018,0019,001A,001C,0020,0021,0022,0023,0024,0025,0026,0027,0028,0029,002B,002C,002D,002E,002F,0030,0031,0034,0036,0037,0038,0039,003A,003B,003D,0064,0068,006E,0070,0074,0075,0078,0079,007A,007C,007D,0080,0081,0083,0089,008C,008D,0093,0094,0096,0097,0099,009A,009B,009C,009D,009E,00C0,00C1,00C2,00C3,00C4,00C5,00C6,00C7,00C8,00C9,00CA,00CC,00CD,00D1,00D6,00D7,00D8,00DA,00DC,00DD,00DE,00E2,00E4,00E6,00E8,00EA,00ED,00F0,00F1,00F2,00F3,00F5,00F6,00F9,00FA,00FB,00FC,010F,0120,0123,0125,0127,0128,0129,012A,012C,012F,0132,0133,0134,0137,0138,013D,0140,0141,0142,0143,0160,0161,0162,0163,0165,016C,016E,016F,0177,0179,017D,018B,01A0,01A1,01A2,01A4,01A6,01A9,01AA,01AC,01AD,01AE,01AF,01B1,01B2,01B3,01B4,01B8,01BB,01BC,01BD,01C2,01E0,01E1,01E2,01E3,01E4,01E5,01E6,01E7,01EA,01EB,01EC,01ED,01EF,01F0,01F1,01F3,01F4,01FC,0202,0203,0204,0207,0209,020A,0216,0220,0221,0223,0224,0225,0244,0262,026B,026C,026D,026E,0270,0280,0282,0283,0286,029B,029C"
        },
        {
          "physicalPackageNumber": 0,
          "physicalProcessorNumber": 6,
          "efficiency": 0,
          "idString": "cpu:type:x86,ven0002fam0019mod0050:feature:,0000,0001,0002,0003,0004,0005,0006,0007,0008,0009,000B,000C,000D,000E,000F,0010,0011,0013,0017,0018,0019,001A,001C,0020,0021,0022,0023,0024,0025,0026,0027,0028,0029,002B,002C,002D,002E,002F,0030,0031,0034,0036,0037,0038,0039,003A,003B,003D,0064,0068,006E,0070,0074,0075,0078,0079,007A,007C,007D,0080,0081,0083,0089,008C,008D,0093,0094,0096,0097,0099,009A,009B,009C,009D,009E,00C0,00C1,00C2,00C3,00C4,00C5,00C6,00C7,00C8,00C9,00CA,00CC,00CD,00D1,00D6,00D7,00D8,00DA,00DC,00DD,00DE,00E2,00E4,00E6,00E8,00EA,00ED,00F0,00F1,00F2,00F3,00F5,00F6,00F9,00FA,00FB,00FC,010F,0120,0123,0125,0127,0128,0129,012A,012C,012F,0132,0133,0134,0137,0138,013D,0140,0141,0142,0143,0160,0161,0162,0163,0165,016C,016E,016F,0177,0179,017D,018B,01A0,01A1,01A2,01A4,01A6,01A9,01AA,01AC,01AD,01AE,01AF,01B1,01B2,01B3,01B4,01B8,01BB,01BC,01BD,01C2,01E0,01E1,01E2,01E3,01E4,01E5,01E6,01E7,01EA,01EB,01EC,01ED,01EF,01F0,01F1,01F3,01F4,01FC,0202,0203,0204,0207,0209,020A,0216,0220,0221,0223,0224,0225,0244,0262,026B,026C,026D,026E,0270,0280,0282,0283,0286,029B,029C"
        },
        {
          "physicalPackageNumber": 0,
          "physicalProcessorNumber": 7,
          "efficiency": 0,
          "idString": "cpu:type:x86,ven0002fam0019mod0050:feature:,0000,0001,0002,0003,0004,0005,0006,0007,0008,0009,000B,000C,000D,000E,000F,0010,0011,0013,0017,0018,0019,001A,001C,0020,0021,0022,0023,0024,0025,0026,0027,0028,0029,002B,002C,002D,002E,002F,0030,0031,0034,0036,0037,0038,0039,003A,003B,003D,0064,0068,006E,0070,0074,0075,0078,0079,007A,007C,007D,0080,0081,0083,0089,008C,008D,0093,0094,0096,0097,0099,009A,009B,009C,009D,009E,00C0,00C1,00C2,00C3,00C4,00C5,00C6,00C7,00C8,00C9,00CA,00CC,00CD,00D1,00D6,00D7,00D8,00DA,00DC,00DD,00DE,00E2,00E4,00E6,00E8,00EA,00ED,00F0,00F1,00F2,00F3,00F5,00F6,00F9,00FA,00FB,00FC,010F,0120,0123,0125,0127,0128,0129,012A,012C,012F,0132,0133,0134,0137,0138,013D,0140,0141,0142,0143,0160,0161,0162,0163,0165,016C,016E,016F,0177,0179,017D,018B,01A0,01A1,01A2,01A4,01A6,01A9,01AA,01AC,01AD,01AE,01AF,01B1,01B2,01B3,01B4,01B8,01BB,01BC,01BD,01C2,01E0,01E1,01E2,01E3,01E4,01E5,01E6,01E7,01EA,01EB,01EC,01ED,01EF,01F0,01F1,01F3,01F4,01FC,0202,0203,0204,0207,0209,020A,0216,0220,0221,0223,0224,0225,0244,0262,026B,026C,026D,026E,0270,0280,0282,0283,0286,029B,029C"
        }
      ],
      "processorIdentifier": {
        "cpu64bit": true,
        "microarchitecture": "Zen 3",
        "stepping": "0",
        "vendorFreq": 4507000000,
        "vendor": "AuthenticAMD",
        "identifier": "AuthenticAMD Family 25 Model 80 Stepping 0",
        "family": "25",
        "model": "80",
        "name": "AMD Ryzen 7 PRO 5850U with Radeon Graphics"
      }
    },
    "memory": {
      "total": 29237960704,
      "virtualMemory": {
        "swapPagesIn": 52927,
        "swapPagesOut": 394458,
        "swapTotal": 2147479552,
        "swapUsed": 904048640,
        "virtualMax": 16766459904,
        "virtualInUse": 15526797312
      },
      "pageSize": 4096,
      "available": 14615212032,
      "physicalMemory": []
    },
    "gpu": [
      {
        "name": "Cezanne [Radeon Vega Series / Radeon Vega Mobile Series]",
        "deviceId": "0x1638",
        "vendor": "Advanced Micro Devices, Inc. [AMD/ATI] (0x1002)",
        "versionInfo": "unknown",
        "vram": 270532608
      }
    ],
    "java": {
      "java.vendor": "JetBrains s.r.o.",
      "java.vm.specification.vendor": "Oracle Corporation",
      "java.version": "17.0.7",
      "java.vm.version": "17.0.7+7-b1000.6",
      "java.vm.name": "OpenJDK 64-Bit Server VM",
      "java.vm.specification.version": "17",
      "java.vm.specification.name": "Java Virtual Machine Specification",
      "java.vm.vendor": "JetBrains s.r.o."
    }
  }
}
```
</details>

## Related tools
As we have noted in our paper, benchmarks often do not capture the overhead caused by the runtime environment. If you want to see how the overhead affects performance, you can use [hashcat](https://hashcat.net/hashcat/). The options that come closest to the benchmarking performed by this plugin are:

```
hashcat -m 1400 -D 1 -b --cpu-affinity=1 -w 1 # SHA2-256, kernel optimization disabled, single CPU
``` 

Running hashcat with these options on an AMD Ryzen 7 PRO 5850U gave the following results:

<details>

```
hashcat (v6.2.6) starting in benchmark mode

OpenCL API (OpenCL 3.0 PoCL 5.0+debian  Linux, None+Asserts, RELOC, SPIR, LLVM 16.0.6, SLEEF, DISTRO, POCL_DEBUG) - Platform #1 [The pocl project]
==================================================================================================================================================
* Device #1: cpu-haswell-AMD Ryzen 7 PRO 5850U with Radeon Graphics, 12885/25835 MB (4096 MB allocatable), 16MCU

Benchmark relevant options:
===========================
* --opencl-device-types=1
* --workload-profile=1

---------------------------
* Hash-Mode 1400 (SHA2-256)
---------------------------

Speed.#1.........: 24930.3 kH/s (1.09ms) @ Accel:32 Loops:64 Thr:1 Vec:8

Started: Wed Nov  6 22:21:43 2024
Stopped: Wed Nov  6 22:21:49 2024
```
</details>

While running the plugin gave these results:

<details>

```json
{
  "runsPerImpl": 5,
  "warmupRunsPerImpl": 1,
  "iterations": 2500000,
  "pauseBetweenRuns": 30000,
  "input": "mmustermd8L_sA$9",
  "results": [
    {
      "implementation": "BouncyCastleSHA256",
      "hash": "3cd28e33a05b816fd9625048ed7d0466e640fae75b2c54058deff0ec2a7b962a",
      "elapsedMillisecondsMean": 749.1354890199999,
      "elapsedMillisecondsStd": 13.368216600274456,
      "hashesPerSecondMean": 3338213.7143915812,
      "hashesPerSecondStd": 57950.2041334752
    },
    {
      "implementation": "JRESHA256",
      "hash": "3cd28e33a05b816fd9625048ed7d0466e640fae75b2c54058deff0ec2a7b962a",
      "elapsedMillisecondsMean": 162.13316344,
      "elapsedMillisecondsStd": 3.203018146042761,
      "hashesPerSecondMean": 1.542536318794936E7,
      "hashesPerSecondStd": 300719.91718990944
    }
  ],
  "sysInfo": {}
}
```
</details>

When comparing the plugin (15.4 MH/s, JRESHA256) with hashcat (24.93 MH/s), we see that hashcat can be roughly 60% faster. We also ran hashcat with optimized kernel code and observed that it was roughly 150% faster (38,7 MH/s).