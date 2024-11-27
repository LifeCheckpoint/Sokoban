#include <pch.h>
#include <jni.h>
#include <commdlg.h>
#include <windows.h>
#include <string>   // for std::wstring
#include <iostream> // for standard input/output
#include <com_sokoban_utils_WindowsFileChooser.h>

JNIEXPORT jstring JNICALL Java_com_sokoban_utils_WindowsFileChooser_openFileChooser(JNIEnv* env, jclass cls, jstring filterString) {
    OPENFILENAMEW ofn;  
    wchar_t szFile[260];

    // Initialize OPENFILENAMEA structure
    ZeroMemory(&ofn, sizeof(ofn));
    ofn.lStructSize = sizeof(ofn);
    ofn.lpstrFile = szFile;
    ofn.lpstrFile[0] = L'\0';
    ofn.nMaxFile = sizeof(szFile);
    //ofn.lpstrFilter = L"Map Files (*.map, *.cmap)\0*.map;*.cmap\0All Files (*.*)\0*.*\0";

    // 从 Java 获取 UTF-8 编码的过滤字符串
    const char* filterCStr = env->GetStringUTFChars(filterString, nullptr);

    // 转换为宽字符
    size_t filterLen = strlen(filterCStr);
    size_t wideCharLen = MultiByteToWideChar(CP_UTF8, 0, filterCStr, -1, NULL, 0);
    std::wstring filterWStr(wideCharLen, L'\0');
    MultiByteToWideChar(CP_UTF8, 0, filterCStr, -1, &filterWStr[0], wideCharLen);

    // 替换分隔符 '|' 为 null 字符 '\0'
    for (auto& ch : filterWStr) {
        if (ch == L'|') {
            ch = L'\0';
        }
    }

    // 释放 Java 字符串资源
    env->ReleaseStringUTFChars(filterString, filterCStr);

    // 设置过滤器
    ofn.lpstrFilter = filterWStr.c_str();

    ofn.nFilterIndex = 1;
    ofn.lpstrFileTitle = NULL;
    ofn.nMaxFileTitle = 0;
    ofn.lpstrInitialDir = NULL;
    ofn.Flags = OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST;

    // Display the Open dialog box
    if (GetOpenFileName(&ofn) == TRUE) {
        // File selected, convert char* path to jstring and return it
      return env->NewString(
          (const jchar*)ofn.lpstrFile,
          wcslen(ofn.lpstrFile));  // 使用 NewString 转换宽字符
    } else {
        // No file selected, return null
        return NULL;
    }
}

JNIEXPORT jstring JNICALL Java_com_sokoban_utils_WindowsFileChooser_saveFileChooser(JNIEnv* env, jclass cls, jstring filterString) {
    OPENFILENAMEW ofn;
    wchar_t szFile[260] = L"NewMap.map";

    // Initialize OPENFILENAMEW structure
    ZeroMemory(&ofn, sizeof(ofn));
    ofn.lStructSize = sizeof(ofn);
    ofn.lpstrFile = szFile;
    ofn.lpstrFile[0] = L'\0';
    ofn.nMaxFile = sizeof(szFile) / sizeof(wchar_t);

    // 从 Java 获取 UTF-8 编码的过滤字符串
    const char* filterCStr = env->GetStringUTFChars(filterString, nullptr);

    // 转换为宽字符
    size_t filterLen = strlen(filterCStr);
    size_t wideCharLen = MultiByteToWideChar(CP_UTF8, 0, filterCStr, -1, NULL, 0);
    std::wstring filterWStr(wideCharLen, L'\0');
    MultiByteToWideChar(CP_UTF8, 0, filterCStr, -1, &filterWStr[0], wideCharLen);

    // 替换分隔符 '|' 为 null 字符 '\0'
    for (auto& ch : filterWStr) {
        if (ch == L'|') {
            ch = L'\0';
        }
    }

    // 释放 Java 字符串资源
    env->ReleaseStringUTFChars(filterString, filterCStr);

    // 设置过滤器
    ofn.lpstrFilter = filterWStr.c_str();
    ofn.nFilterIndex = 1;
    ofn.lpstrFileTitle = NULL;
    ofn.nMaxFileTitle = 0;
    ofn.lpstrInitialDir = NULL;

    // 设置保存对话框特定的标志
    ofn.Flags = OFN_PATHMUSTEXIST | OFN_OVERWRITEPROMPT;

    // Display the Save dialog box
    if (GetSaveFileName(&ofn) == TRUE) {
        // 文件已选择，返回文件路径
        return env->NewString((const jchar*)ofn.lpstrFile, wcslen(ofn.lpstrFile));
    } else {
        // 没有选择文件，返回 null
        return NULL;
    }
}
