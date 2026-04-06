# Composer SDK - Consumer ProGuard Rules
# These rules are automatically applied to apps consuming this library

# Keep all public interfaces that consumers implement
-keep interface com.debdut.composer.action.Action { *; }
-keep interface com.debdut.composer.action.StoreAction { *; }
-keep interface com.debdut.composer.action.ComposerAction { *; }
-keep interface com.debdut.composer.action.DataComposerAction { *; }
-keep interface com.debdut.composer.action.UIComposerAction { *; }
-keep interface com.debdut.composer.action.ActionId { *; }
-keep interface com.debdut.composer.state.UIState { *; }
-keep interface com.debdut.composer.state.UIStateType { *; }
-keep interface com.debdut.composer.composer.ui.WidgetId { *; }
-keep interface com.debdut.composer.composer.ui.HostWidgetId { *; }
-keep interface com.debdut.composer.composer.ui.ChildWidgetId { *; }
-keep interface com.debdut.composer.composer.ui.NoStoreWidgetId { *; }
-keep interface com.debdut.composer.store.StoreId { *; }
-keep interface com.debdut.composer.store.StoreInitObj { *; }
-keep interface com.debdut.composer.store.StoreWidgetModel { *; }
-keep interface com.debdut.composer.composer.data.DataComposerActionHandler { *; }

# Keep abstract classes consumers extend
-keep class com.debdut.composer.store.Store { *; }
-keep class com.debdut.composer.store.factory.StoreFactory { *; }
-keep class com.debdut.composer.viewmodel.** { *; }
-keep class com.debdut.composer.uicomponents.** { *; }

# Keep action holders (used in callbacks)
-keep class com.debdut.composer.action.holder.** { *; }

# Keep state type markers
-keep class com.debdut.composer.state.UIStateDefaultType { *; }
-keep class com.debdut.composer.state.HeaderUIStateType { *; }
-keep class com.debdut.composer.state.FooterUIStateType { *; }
-keep class com.debdut.composer.state.NoOpsUIState { *; }

# Keep data classes used in public API
-keep class com.debdut.composer.composer.ui.GroupWidget { *; }
-keep class com.debdut.composer.composer.data.model.StoreActionWidgetIdPair { *; }

# Keep composer interfaces
-keep interface com.debdut.composer.composer.Composer { *; }
-keep interface com.debdut.composer.composer.data.DataComposer { *; }
-keep interface com.debdut.composer.composer.data.ListDataComposer { *; }
-keep interface com.debdut.composer.composer.data.SingleDataComposer { *; }
-keep interface com.debdut.composer.composer.data.ListWithHeaderAndFooterDataComposer { *; }
-keep interface com.debdut.composer.composer.ui.UIComposer { *; }
-keep interface com.debdut.composer.composer.ui.ListUIComposer { *; }
-keep interface com.debdut.composer.composer.ui.SingleUIComposer { *; }
-keep interface com.debdut.composer.composer.ui.ListUIComposerWithHeaderAndFooter { *; }

# Keep host interfaces
-keep interface com.debdut.composer.composer.data.host.** { *; }
