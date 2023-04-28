export interface BackgroundModePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
