# Gallery Sample

- [안드로이드 공식 샘플: storage-samples/MediaStore](https://github.com/android/storage-samples/tree/master/MediaStore)를 기반으로
  - `viewBinding` 적용
  - `ActivityCompat.requestPermissions` 대신 `registerForActivityResult` 적용, 권한 받고 이미지를 보여주는 로직 간소화
  - `Adapter`와 `ViewHolder`를 `MainActivity`에서 분리
  - `contentResolver.query`에서 사용하지 않는 `selection`, `selectionArgs` 삭제

## 참고 사이트

- [안드로이드 공식 문서: 공유 저장소의 미디어 파일에 액세스](https://developer.android.com/training/data-storage/shared/media)
- [안드로이드 공식 샘플: storage-samples/MediaStore](https://github.com/android/storage-samples/tree/master/MediaStore)