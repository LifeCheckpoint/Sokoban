#include <pch.h>
#include <jni.h>
#include <commdlg.h>
#include <windows.h>
#include <com_sokoban_utils_WindowsFileChooser.h>

JNIEXPORT jstring JNICALL Java_com_sokoban_utils_WindowsFileChooser_openFileChooser(JNIEnv* env, jclass cls, jobjectArray extensionsArray) {
    OPENFILENAMEW ofn;  
    wchar_t szFile[260];

    // Initialize OPENFILENAMEA structure
    ZeroMemory(&ofn, sizeof(ofn));
    ofn.lStructSize = sizeof(ofn);
    ofn.lpstrFile = szFile;
    ofn.lpstrFile[0] = L'\0';
    ofn.nMaxFile = sizeof(szFile);
    ofn.lpstrFilter = L"Map Files (*.map, *.cmap)\0*.map;*.cmap\0All Files (*.*)\0*.*\0";
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
