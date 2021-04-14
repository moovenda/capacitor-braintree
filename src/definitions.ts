export interface BraintreePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
